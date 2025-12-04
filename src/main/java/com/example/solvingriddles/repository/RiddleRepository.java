package com.example.solvingriddles.repository;

import com.example.solvingriddles.model.Riddle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RiddleRepository {

    // データを保管する箱
    private final Map<Integer, Riddle> storage = new HashMap<>();
    
    // JSONを変換する道具
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * アプリ起動時に1回だけ呼ばれるメソッド
     * JSONファイルを読んで storage に詰め込む
     * @throws IOException ファイル読み込みに失敗した場合
     */
    @PostConstruct
    public void init() {
        try {
            // riddles.json を読み込む
            ClassPathResource resource = new ClassPathResource("data/riddles.json");
            
            // JSON → JavaのList に変換
            List<Riddle> riddles = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<List<Riddle>>() {}
            );

            // List を Map に詰め替え (検索しやすくするため)
            riddles.forEach(riddle -> storage.put(riddle.id(), riddle));
            
            System.out.println("謎解きデータをロードしました: " + riddles.size() + "件");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("データの読み込みに失敗したわ", e);
        }
    }

    /**
     * IDで検索
     * @param id 謎解きID
     * @return 該当する謎解きデータ (存在しない場合は空のOptional)
     */
    public Optional<Riddle> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * 全件取得
     * @return 全ての謎解きデータのリスト
     */
    public List<Riddle> findAll() {
        return new ArrayList<>(storage.values());
    }
}