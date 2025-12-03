package com.example.solvingriddles;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.service.RiddleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class) // Controllerのテスト専用モード
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc; // ブラウザの代わりをしてくれる君

    @MockitoBean // ServiceのニセモノをSpringに登録する
    private RiddleService riddleService;

    @Test
    @DisplayName("クイズ画面: IDが存在すれば画面とデータを返す")
    void testQuizFound() throws Exception {
        // 準備: ID 1ならデータを返すように仕込む
        Riddle mockRiddle = new Riddle(1, "Question", "Answer", "Hint");
        when(riddleService.findById(1)).thenReturn(Optional.of(mockRiddle));

        // 実行 & 検証
        mockMvc.perform(get("/quiz/1"))
                .andExpect(status().isOk()) // HTTP 200 OKか？
                .andExpect(view().name("quiz")) // "quiz.html" を返そうとしてるか？
                .andExpect(model().attribute("riddle", mockRiddle)); // データは渡せてるか？
    }

    @Test
    @DisplayName("クイズ画面: IDが存在しないなら一覧へリダイレクト")
    void testQuizNotFound() throws Exception {
        // 準備: ID 999なら「ない（Empty）」を返す
        when(riddleService.findById(999)).thenReturn(Optional.empty());

        // 実行 & 検証
        mockMvc.perform(get("/quiz/999"))
                .andExpect(status().is3xxRedirection()) // リダイレクトしようとしてるか？
                .andExpect(redirectedUrl("/list")); // 行き先は一覧か？
    }

    @Test
    @DisplayName("答え合わせ: 正解なら成功フラグを渡す")
    void testCheckAnswerSuccess() throws Exception {
        // 準備: ID 1, Answerなら「正解(true)」
        when(riddleService.checkAnswer(1, "Answer")).thenReturn(true);

        // 実行 (POST送信)
        mockMvc.perform(post("/quiz/check")
                        .param("id", "1")
                        .param("answer", "Answer"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("isSuccess", true));
    }

    @Test
    @DisplayName("答え合わせ: 間違いなら失敗フラグを渡す")
    void testCheckAnswerFailure() throws Exception {
        // 準備: ID 1, WrongAnswerなら「不正解(false)」
        when(riddleService.checkAnswer(1, "WrongAnswer")).thenReturn(false);

        // 実行
        mockMvc.perform(post("/quiz/check")
                        .param("id", "1")
                        .param("answer", "WrongAnswer"))
                .andExpect(status().isOk())
                .andExpect(view().name("result")) // 画面は同じ result.html やけど...
                .andExpect(model().attribute("isSuccess", false)) // フラグは false か？
                .andExpect(model().attribute("resultTitle", "ACCESS DENIED")); // メッセージは拒否か？
    }
}

