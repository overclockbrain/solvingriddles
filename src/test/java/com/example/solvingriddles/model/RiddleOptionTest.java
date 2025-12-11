package com.example.solvingriddles.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiddleOptionTest {

    @Test
    @DisplayName("RiddleOptionが正しく生成され、値が取得できること")
    void testCreateAndRead() {
        // 1. インスタンス生成 (text, role)
        RiddleOption option = new RiddleOption("O", "target");

        // 2. 値の検証
        assertEquals("O", option.text());
        assertEquals("target", option.role());
    }

    @Test
    @DisplayName("Recordの特性: 同じ値を持つインスタンスは等価(equals)であること")
    void testEquality() {
        // 全く同じ値を持つ2つのインスタンス
        RiddleOption opt1 = new RiddleOption("A", "fake");
        RiddleOption opt2 = new RiddleOption("A", "fake");
        
        // 値が違うインスタンス
        RiddleOption opt3 = new RiddleOption("B", "fake");     // 文字違い
        RiddleOption opt4 = new RiddleOption("A", "normal");   // 役割違い

        // 検証
        assertEquals(opt1, opt2, "同じ値なら equals は true になるはず！");
        assertNotEquals(opt1, opt3, "文字が違うなら equals は false！");
        assertNotEquals(opt1, opt4, "役割が違うなら equals は false！");
        
        // hashCodeもチェック
        assertEquals(opt1.hashCode(), opt2.hashCode());
    }
}