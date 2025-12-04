package com.example.solvingriddles;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.service.RiddleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * アプリ全体の画面遷移を管理するコントローラー
 * ユーザーからのリクエストを受け付け、Service層に処理を依頼し、
 * 結果に応じたHTMLテンプレートを返却する役割を持つ。
 */
@Controller
public class MainController {

    private final RiddleService riddleService;

    /**
     * コンストラクタ
     * Springの依存性注入(DI)により、自動的にRiddleServiceが渡される。
     * @param riddleService 謎解きのロジックを担当するサービス
     */
    public MainController(RiddleService riddleService) {
        this.riddleService = riddleService;
    }

    /**
     * トップ画面を表示する
     * @return トップ画面のHTMLファイル名 (index.html)
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 謎解き一覧画面を表示する
     * Service層から全ての謎解きデータを取得し、HTMLに渡す。
     * @return 一覧画面のHTMLファイル名 (list.html)
     */
    @GetMapping("/list")
    public String list(Model model) {
        // ★ここが大事！Serviceから全データを取ってきて...
        // (import java.util.List; を忘れずに！)
        List<Riddle> riddles = riddleService.findAll();
        
        // ★ "riddles" という名前でHTMLに渡す！
        model.addAttribute("riddles", riddles);
        
        return "list";
    }

    /**
     * 指定されたIDの謎解き画面を表示する
     * URLの {id} 部分を数値として受け取り、対応する問題データを検索する。
     * データが存在しない場合は一覧画面へリダイレクトする。
     *
     * @param id    URLから取得した問題ID (例: /quiz/1 なら 1)
     * @param model 画面(HTML)にデータを渡すための入れ物
     * @return 謎解き画面 (quiz.html)、または一覧へのリダイレクトパス
     */
    @GetMapping("/quiz/{id}")
    public String quiz(@PathVariable Integer id, Model model) {
        // Serviceを使って問題データを取得
        Optional<Riddle> riddle = riddleService.findById(id);

        // もし存在しないIDなら、一覧画面に強制送還（リダイレクト）
        if (riddle.isEmpty()) {
            return "redirect:/list";
        }

        // HTML側で "riddle" という名前でデータを使えるようにする
        model.addAttribute("riddle", riddle.get());
        return "quiz";
    }

    /**
     * ユーザーの回答を受け取り、正誤判定を行う
     * 判定ロジックはService層に委譲し、その結果に応じて画面表示用のメッセージを設定する。
     *
     * @param id     回答対象の問題ID
     * @param answer フォームから送信された回答文字列
     * @param model  画面に結果を表示するためのデータ受け渡し用
     * @return 結果画面のHTMLファイル名 (result.html)
     */
    @PostMapping("/quiz/check")
    public String check(@RequestParam Integer id, @RequestParam String answer, Model model) {
        // 判定ロジックはServiceに丸投げ
        boolean isSuccess = riddleService.checkAnswer(id, answer);

        if (isSuccess) {
            model.addAttribute("resultTitle", "ACCESS GRANTED");
            model.addAttribute("resultMessage", "認証成功。システムロックが解除されました。");
            model.addAttribute("isSuccess", true);
        } else {
            model.addAttribute("resultTitle", "ACCESS DENIED");
            model.addAttribute("resultMessage", "不正なキーです。セキュリティアラート作動。");
            model.addAttribute("isSuccess", false);
        }
        
        // リトライ用にIDも渡しておく (result.htmlから戻るため)
        model.addAttribute("riddleId", id);
        
        return "result";
    }
}