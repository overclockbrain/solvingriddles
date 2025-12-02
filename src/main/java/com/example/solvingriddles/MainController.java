package com.example.solvingriddles;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * アプリ全体の画面遷移を管理するコントローラー
 */
@Controller
public class MainController {

    /**
     * トップ画面を表示する
     * @return トップ画面のHTML名 (index.html)
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 謎解き一覧画面を表示する (★ここを追加！)
     * @return 一覧画面のHTML名 (list.html)
     */
    @GetMapping("/list")
    public String list() {
        return "list";
    }

    /**
     * 謎解き（第1問）の画面を表示する
     * @return 謎解き画面のHTML名 (quiz.html)
     */
    @GetMapping("/quiz")
    public String quiz() {
        return "quiz";
    }

    /**
     * ユーザーの回答を判定して、結果画面を表示する
     * @param answer フォームから送信された回答文字列
     * @param model  画面に結果を表示するためのデータ受け渡し用
     * @return 結果画面のHTML名 (result.html)
     */
    @PostMapping("/quiz/check")
    public String check(@RequestParam String answer, Model model) {
        // 正解は "t" か "T" (大文字小文字無視)
        if ("t".equalsIgnoreCase(answer)) {
            model.addAttribute("resultTitle", "ACCESS GRANTED");
            model.addAttribute("resultMessage", "認証成功。システムロックが解除されました。");
            model.addAttribute("isSuccess", true); // 成功フラグ
        } else {
            model.addAttribute("resultTitle", "ACCESS DENIED");
            model.addAttribute("resultMessage", "不正なキーです。セキュリティアラート作動。");
            model.addAttribute("isSuccess", false); // 失敗フラグ
        }
        return "result";
    }
}