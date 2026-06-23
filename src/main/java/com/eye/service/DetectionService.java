package com.eye.service;

import com.eye.entity.AbnormalEvent;
import com.eye.entity.DetectionRecord;
import com.eye.entity.Favorite;
import com.eye.repository.AbnormalEventRepository;
import com.eye.repository.DetectionRecordRepository;
import com.eye.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class DetectionService {
    private final DetectionRecordRepository recordRepo;
    private final AbnormalEventRepository abnormalRepo;
    private final FavoriteRepository favoriteRepo;

    public DetectionService(DetectionRecordRepository recordRepo,
                            AbnormalEventRepository abnormalRepo,
                            FavoriteRepository favoriteRepo) {
        this.recordRepo = recordRepo;
        this.abnormalRepo = abnormalRepo;
        this.favoriteRepo = favoriteRepo;
    }

    public List<DetectionRecord> getHistory(String username) {
        return recordRepo.findByUsernameOrderByTimestampDesc(username);
    }

    public List<DetectionRecord> getHistoryLight(String username) {
        List<DetectionRecord> records = recordRepo.findByUsernameOrderByTimestampDesc(username);
        for (DetectionRecord r : records) {
            r.setPhoto("");
        }
        return records;
    }

    public String getPhoto(String username, String recordId) {
        var list = recordRepo.findByUsernameAndRecordId(username, recordId);
        if (!list.isEmpty()) {
            String photo = list.get(0).getPhoto();
            return photo != null ? photo : "";
        }
        return "";
    }

    public DetectionRecord saveRecord(DetectionRecord record) {
        return recordRepo.save(record);
    }

    @Transactional
    public void deleteRecord(String username, String recordId) {
        recordRepo.deleteByUsernameAndRecordId(username, recordId);
    }

    public List<AbnormalEvent> getAbnormalEvents(String username) {
        return abnormalRepo.findByUsernameOrderByTimestampDesc(username);
    }

    public List<AbnormalEvent> getRecentAbnormalEvents(String username) {
        return abnormalRepo.findTop5ByUsernameOrderByTimestampDesc(username);
    }

    public AbnormalEvent saveAbnormalEvent(AbnormalEvent event) {
        return abnormalRepo.save(event);
    }

    @Transactional
    public void clearAbnormalEvents(String username) {
        var events = abnormalRepo.findByUsernameOrderByTimestampDesc(username);
        abnormalRepo.deleteAll(events);
    }

    public Map<String, Object> saveDetectionWithEvents(String username, Map<String, Object> body) {
        String recordId = (String) body.get("id");
        DetectionRecord record = new DetectionRecord();
        record.setRecordId(recordId != null ? recordId : String.valueOf(System.currentTimeMillis()));
        record.setTimestamp((Long) body.get("timestamp"));
        record.setPhoto((String) body.get("photo"));
        record.setResults(body.get("results") != null ? body.get("results").toString() : "[]");
        record.setAvgConfidence((Double) body.get("avgConfidence"));
        record.setUsername(username);
        saveRecord(record);

        if (body.containsKey("abnormalEvents")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> events = (List<Map<String, Object>>) body.get("abnormalEvents");
            if (events != null) {
                for (Map<String, Object> e : events) {
                    AbnormalEvent ae = new AbnormalEvent();
                    ae.setEventId((String) e.get("id"));
                    ae.setTimestamp((Long) e.get("timestamp"));
                    ae.setPersonName((String) e.get("personName"));
                    ae.setBehavior((String) e.get("behavior"));
                    ae.setBehaviorLabel((String) e.get("behaviorLabel"));
                    ae.setLocation((String) e.get("location"));
                    ae.setConfidence((Double) e.get("confidence"));
                    ae.setSourceHistoryId((String) e.get("sourceHistoryId"));
                    ae.setUsername(username);
                    saveAbnormalEvent(ae);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    // Favorites
    public List<DetectionRecord> getFavorites(String username) {
        List<Favorite> favs = favoriteRepo.findByUsername(username);
        List<DetectionRecord> records = new ArrayList<>();
        for (Favorite fav : favs) {
            var list = recordRepo.findByUsernameAndRecordId(username, fav.getRecordId());
            if (!list.isEmpty()) {
                records.add(list.get(0));
            }
        }
        return records;
    }

    public Map<String, Object> addFavorite(String username, String recordId) {
        Map<String, Object> result = new HashMap<>();
        if (!favoriteRepo.existsByUsernameAndRecordId(username, recordId)) {
            Favorite fav = new Favorite();
            fav.setUsername(username);
            fav.setRecordId(recordId);
            favoriteRepo.save(fav);
        }
        result.put("success", true);
        return result;
    }

    @Transactional
    public Map<String, Object> removeFavorite(String username, String recordId) {
        favoriteRepo.deleteByUsernameAndRecordId(username, recordId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    public List<String> getAllStudentNames(String username) {
        List<DetectionRecord> records = recordRepo.findByUsernameOrderByTimestampDesc(username);
        Set<String> names = new HashSet<>();
        for (DetectionRecord record : records) {
            String resultsJson = record.getResults();
            if (resultsJson != null) {
                names.addAll(extractClassNames(resultsJson));
            }
        }
        if (names.isEmpty()) {
            return Arrays.asList("李某某", "张某某", "王某某", "赵某某", "刘某某");
        }
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        return sorted;
    }

    private List<String> extractClassNames(String resultsJson) {
        List<String> names = new ArrayList<>();
        try {
            var arr = new ObjectMapper().readTree(resultsJson);
            for (var node : arr) {
                var cn = node.get("className");
                if (cn != null) names.add(cn.asText());
            }
        } catch (Exception e) {
            // ignore parse errors
        }
        return names;
    }
}