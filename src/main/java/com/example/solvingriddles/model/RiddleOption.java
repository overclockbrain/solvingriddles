package com.example.solvingriddles.model;

/**
 * クリック問題の選択肢（文章のパーツ）
 * @param text 表示する文字 (例: "O", "E R R ")
 * @param role 役割 ("target"=正解, "fake"=ダミー, "normal"=ただの文字)
 */
public record RiddleOption(
    String text,
    String role 
) {}
