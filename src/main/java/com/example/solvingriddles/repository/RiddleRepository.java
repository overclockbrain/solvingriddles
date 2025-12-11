package com.example.solvingriddles.repository;

import com.example.solvingriddles.model.Riddle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RiddleRepository {

    private final ObjectMapper objectMapper;
    
    // "HACKER" -> [...], "CASUAL" -> [...] のように管理
    private final Map<String, List<Riddle>> riddleMap = new ConcurrentHashMap<>();

    /**
     * コンストラクタ
     * @param objectMapper
     */
    public RiddleRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * アプリ起動時にJSONファイルを読み込む
     */
    @PostConstruct
    public void init() {
        // ファイル名は後で実際に作る名前に合わせてな！
        riddleMap.put("HACKER", loadJson("data/hacker_riddles.json"));
        riddleMap.put("CASUAL", loadJson("data/casual_riddles.json"));
    }

    /**
     * JSONファイルを読み込んでRiddleリストを返す
     * @param path
     * @return
     */
    private List<Riddle> loadJson(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                System.out.println("⚠️ ファイルが見つかりません: " + path);
                return Collections.emptyList();
            }
            InputStream is = resource.getInputStream();
            return objectMapper.readValue(is, new TypeReference<List<Riddle>>() {});
        } catch (IOException e) {
            // エラーログ出して空リストで続行（アプリを落とさない）
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * モードを指定して全件取得
     * @return 謎解きリスト
     */
    public List<Riddle> findAll(String mode) {
        return riddleMap.getOrDefault(mode, Collections.emptyList());
    }

    /**
     * モードとIDで検索
     * @return 1件または空
     */
    public Optional<Riddle> findById(String mode, Integer id) {
        return findAll(mode).stream()
                .filter(r -> r.id().equals(id))
                .findFirst();
    }
}