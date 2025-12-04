package com.example.solvingriddles.service;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.repository.RiddleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // ← これ忘れたらただの普通のクラスやから注意な！
public class RiddleService {

    private final RiddleRepository repository;

    /**
     * コンストラクタ
     * @param repository 謎解きデータの保管庫
     */
    public RiddleService(RiddleRepository repository) {
        this.repository = repository;
    }

    /**
     * 正解かどうかを判定する
     * @param id 問題ID
     * @param answer ユーザーの回答
     * @return 正解ならtrue
     */
    public boolean checkAnswer(Integer id, String answer) {
        // 1. IDで正解データを取り出す
        Optional<Riddle> riddleOpt = repository.findById(id);

        // 2. データがない場合（ありえへんけど）は false
        if (riddleOpt.isEmpty()) {
            return false;
        }

        // 3. 正解と比較 (大文字小文字は無視する)
        String correctAnswer = riddleOpt.get().answer();
        return correctAnswer.equalsIgnoreCase(answer);
    }

    /**
     * IDで問題データを取得する (Controllerのためのパシリ)
     * @param id 問題ID
     * @return 問題データのOptional
     */
    public Optional<Riddle> findById(Integer id) {
        return repository.findById(id);
    }

    /**
     * 全ての謎解きデータを取得する (Controllerのためのパシリ)
     * @return 全謎解きデータのリスト
     */
    public List<Riddle> findAll() {
        return repository.findAll();
    }
}