package com.example.solvingriddles.controller.hacker;


import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.service.RiddleService;
import com.example.solvingriddles.constant.AppConst;
import com.example.solvingriddles.constant.UrlConst;
import com.example.solvingriddles.constant.ViewNames;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * コントローラー (MainController) の動作を検証するテスト
 * MockMvcを使用し、HTTPリクエストに対するレスポンス（ステータスコード、View名、モデルデータ）
 * が正しいかを確認する。Service層はモック化 (@MockBean) して切り離す。
 */
@WebMvcTest(HackerController.class)
class HackerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiddleService riddleService;

    /**
     * クイズ画面表示のテスト: 正常系
     * 条件: 指定したIDの問題が存在する場合
     * 期待値:
     * 1. ステータス 200 OK
     * 2. View名が "HACKER_QUIZ"
     * 3. Modelに "riddle" オブジェクトが含まれていること
     */
    @Test
    @DisplayName("クイズ画面: IDが存在すれば画面とデータを返す")
    void testHackerQuizFound() throws Exception {
        // 準備: ID 1ならデータを返す
        Riddle mockRiddle = new Riddle(1, "Question", "Answer", "Hint", "text", null,1, null,null,null);
        when(riddleService.findById(AppConst.MODE_HACKER,1)).thenReturn(Optional.of(mockRiddle));

        // 実行 & 検証
        mockMvc.perform(get(UrlConst.HACKER_BASE + UrlConst.HACKER_QUIZ + "/1"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.HACKER_QUIZ))
                .andExpect(model().attribute("riddle", mockRiddle));
    }

    /**
     * クイズ画面表示のテスト: 異常系 (データなし)
     * 条件: 指定したIDの問題が存在しない場合
     * 期待値:
     * 1. ステータス 3xx (リダイレクト)
     * 2. リダイレクト先が一覧画面 ("/HACKER_LIST") であること
     */
    @Test
    @DisplayName("クイズ画面: IDが存在しないなら一覧へリダイレクト")
    void testHackerQuizNotFound() throws Exception {
        // 準備: ID 999なら空を返す
        when(riddleService.findById(AppConst.MODE_HACKER,999)).thenReturn(Optional.empty());

        // 実行 & 検証
        mockMvc.perform(get(UrlConst.HACKER_BASE + UrlConst.HACKER_QUIZ + "/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(UrlConst.HACKER_BASE + UrlConst.HACKER_LIST));
    }

    /**
     * 答え合わせのテスト: 正解パターン
     * 条件: Serviceが true (正解) を返す場合
     * 期待値:
     * 1. View名が "HACKER_RESULT"
     * 2. Modelの isSuccess フラグが true であること
     */
    @Test
    @DisplayName("答え合わせ: 正解なら成功フラグを渡す")
    void testCheckAnswerSuccess() throws Exception {
        // 準備
        when(riddleService.checkAnswer(AppConst.MODE_HACKER,1, "Answer")).thenReturn(true);

        // 実行
        mockMvc.perform(post(UrlConst.HACKER_BASE + UrlConst.HACKER_QUIZ_CHECK)
                        .param("id", "1")
                        .param("answer", "Answer"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.HACKER_RESULT))
                .andExpect(model().attribute("isSuccess", true));
    }

    /**
     * 答え合わせのテスト: 不正解パターン
     * 条件: Serviceが false (不正解) を返す場合
     * 期待値:
     * 1. View名が "HACKER_RESULT"
     * 2. Modelの isSuccess フラグが false であること
     * 3. 拒否メッセージ (ACCESS DENIED) が設定されていること
     */
    @Test
    @DisplayName("答え合わせ: 間違いなら失敗フラグを渡す")
    void testCheckAnswerFailure() throws Exception {
        // 準備
        when(riddleService.checkAnswer(AppConst.MODE_HACKER,1, "Wrong")).thenReturn(false);

        // 実行
        mockMvc.perform(post(UrlConst.HACKER_BASE + UrlConst.HACKER_QUIZ_CHECK)
                        .param("id", "1")
                        .param("answer", "Wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.HACKER_RESULT))
                .andExpect(model().attribute("isSuccess", false))
                .andExpect(model().attribute("resultTitle", "ACCESS DENIED"));
    }

    /**
     * 一覧画面のテスト
     * 条件: Serviceが全データを返す場合
     * 期待値:
     * 1. View名が "HACKER_LIST"
     * 2. Modelに "riddles" が含まれていて、中身が正しいこと
     */
    @Test
    @DisplayName("一覧画面: 全データを取得して表示する")
    void testHackerList() throws Exception {
        // 準備: ダミーのリストを作る
        List<Riddle> mockHackerList = List.of(
            new Riddle(1, "Q1", "A", "H", "text", null, 1, null,null,null),
            new Riddle(2, "Q2", "A", "H", "text", null, 2, null,null,null)
        );
        
        // findAll() が呼ばれたらダミーリストを返す
        when(riddleService.findAll(AppConst.MODE_HACKER)).thenReturn(mockHackerList);

        // 実行 & 検証
        mockMvc.perform(get(UrlConst.HACKER_BASE + UrlConst.HACKER_LIST))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewNames.HACKER_LIST))
                .andExpect(model().attribute("riddles", mockHackerList)); // データが渡ってるか？
    }
}