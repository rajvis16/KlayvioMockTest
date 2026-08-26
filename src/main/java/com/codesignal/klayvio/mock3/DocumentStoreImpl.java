package com.codesignal.klayvio.mock3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class DocumentStoreImpl implements DocumentStore {

    private final Map<String, String> contentMap = new HashMap<>(); // documentId -> content
    private final Map<String, Integer> versionMap = new HashMap<>(); // documentId -> versionId
    private final Map<String, Map<Integer, String>> versionContentMap = new HashMap<>(); // documentId -> [versionId1 -> content1, versionId2 -> content2,]

    public DocumentStoreImpl() {
    }

    @Override
    public boolean create(String documentId, String content) {
        if (!contentMap.containsKey(documentId)) {
            contentMap.put(documentId, content);
            versionMap.put(documentId, 1);
            Map<Integer, String> versions = new HashMap<>();
            versions.put(1, content);
            versionContentMap.put(documentId, versions);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean update(String documentId, String content) {

        if (contentMap.containsKey(documentId)) {

            contentMap.put(documentId, content);

            Integer versionId = versionMap.getOrDefault(documentId, 1) + 1;
            versionMap.put(documentId, versionId);

            Map<Integer, String> versions = versionContentMap.get(documentId);

            versions.put(versionId, content);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public String get(String documentId) {
        return contentMap.get(documentId);
    }

    @Override
    public boolean delete(String documentId) {
        return null != contentMap.remove(documentId);
    }

    @Override
    public List<String> findByPrefix(String prefix, int limit) {

        return contentMap.entrySet().stream().filter(entry -> {
            return entry.getKey().startsWith(prefix);
        }).map(Map.Entry::getKey).sorted((o1, o2) -> {
            int cmp = Integer.compare(o1.length(), o2.length());
            if (cmp == 0) {
                return o1.compareTo(o2);
            } else {
                return cmp;
            }
        }).limit(limit).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public int getVersionCount(String documentId) {

        if (!contentMap.containsKey(documentId)) {
            return 0;
        }

        return versionMap.get(documentId);
    }

    @Override
    public String getVersion(String documentId, int version) {

        if (!contentMap.containsKey(documentId)) {
            return null;
        }

        return versionContentMap.get(documentId).get(version);

    }

    @Override
    public boolean restoreVersion(String documentId, int version) {

        if (!contentMap.containsKey(documentId)) {
            return false;
        }

        Map<Integer, String> versions = versionContentMap.get(documentId);
        if (!versions.containsKey(version)) {
            return false;
        }

        String content  = versions.get(version);
        update(documentId, content);

        return true;
    }

    @Override
    public int findLatestVersionWithContent(String documentId, String content) {

        if (!contentMap.containsKey(documentId)) {
            return -1;
        }

        int maxVersion = -1;
        Map<Integer, String> versions = versionContentMap.get(documentId);
        for (Map.Entry<Integer, String> entry : versions.entrySet()) {

            if (entry.getValue().equals(content)) {
                maxVersion = Math.max(maxVersion, entry.getKey());
            }
        }

        return maxVersion;
    }
}