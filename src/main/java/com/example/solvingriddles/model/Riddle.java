package com.example.solvingriddles.model;

/**
 * 謎解きのデータを保持するモデル (不変データ)
 * @param id 問題ID
 * @param question 問題文
 * @param answer 正解
 * @param hint ヒント
 */
public record Riddle(
    Integer id,
    String question,
    String answer,
    String hint
) {}