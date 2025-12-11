package com.example.solvingriddles.controller.hacker;

import com.example.solvingriddles.model.Riddle;
import com.example.solvingriddles.service.RiddleService;
import com.example.solvingriddles.constant.UrlConst;
import com.example.solvingriddles.constant.ViewNames;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * アプリ全体の画面遷移を管理するコントローラー
 * ユーザーからのリクエストを受け付け、Service層に処理を依頼し、
 * 結果に応じたHTMLテンプレートを返却する役割を持つ。
 */
@Controller
@RequestMapping(UrlConst.HACKER_BASE) // ここで "/hacker" を指定
public class HackerController {

    private final RiddleService riddleService;
    private final static String MODE = "HACKER";

    /**
     * コンストラクタ
     * Springの依存性注入(DI)により、自動的にRiddleServiceが渡される。
     * @param riddleService 謎解きのロジックを担当するサービス
     */
    public HackerController(RiddleService riddleService) {
        this.riddleService = riddleService;
    }

    /**
     * トップ画面を表示する
     * @return トップ画面のHTMLファイル名 (index.html)
     */
    @GetMapping(UrlConst.ROOT)
    public String index() {
        return ViewNames.LAUNCHER;
    }

    /**
     * 謎解き一覧画面を表示する
     * Service層から全ての謎解きデータを取得し、HTMLに渡す。
     * @return 一覧画面のHTMLファイル名 (list.html)
     */
    @GetMapping(UrlConst.HACKER_LIST)
    public String list(Model model) {
        // ★ここが大事！Serviceから全データを取ってきて...
        // (import java.util.List; を忘れずに！)
        List<Riddle> riddles = riddleService.findAll(MODE);
        
        // ★ "riddles" という名前でHTMLに渡す！
        model.addAttribute("riddles", riddles);
        
        return ViewNames.HACKER_LIST;
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
    @GetMapping(UrlConst.HACKER_QUIZ + "/{id}")
    public String quiz(@PathVariable Integer id, Model model) {
        // Serviceを使って問題データを取得
        Optional<Riddle> riddle = riddleService.findById(MODE,id);

        if (riddle.isEmpty()) {
            // もし存在しないIDなら、一覧画面に強制送還（リダイレクト）
            // UrlConstを使う理由はブラウザに対して明示的に別のURLに移動するよう指示するため
            return "redirect:" + UrlConst.HACKER_BASE + UrlConst.HACKER_LIST;
        }

        // HTML側で "riddle" という名前でデータを使えるようにする
        model.addAttribute("riddle", riddle.get());
        return ViewNames.HACKER_QUIZ;
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
    @PostMapping(UrlConst.HACKER_QUIZ_CHECK)
    public String check(@RequestParam Integer id, @RequestParam String answer, Model model) {
        // 判定ロジックはServiceに丸投げ
        boolean isSuccess = riddleService.checkAnswer(MODE, id, answer);

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
        
        return ViewNames.HACKER_RESULT;
    }

    /**
     * 画像クリック(GET)用の判定メソッド
     * <area href="/quiz/check-image?id=5&answer=..."> から呼ばれる
     * @param id     問題ID
     * @param answer クリック座標などの回答データ
     * @param model  画面に結果を表示するためのデータ受け渡し用
     * @return 結果画面のHTMLファイル名 (result.html)
     */
    @GetMapping(UrlConst.HACKER_QUIZ_CHECK_IMAGE)
    public String checkImage(@RequestParam Integer id, 
                             @RequestParam String answer, 
                             Model model) {
        
        // ロジックはServiceに丸投げ（既存のメソッドを再利用！）
        boolean isSuccess = riddleService.checkAnswer(MODE, id, answer);

        if (isSuccess) {
            model.addAttribute("resultTitle", "ACCESS GRANTED");
            model.addAttribute("resultMessage", "認証成功。画像解析完了。");
            model.addAttribute("isSuccess", true);
        } else {
            model.addAttribute("resultTitle", "ACCESS DENIED");
            model.addAttribute("resultMessage", "座標エラー。ターゲットではありません。");
            model.addAttribute("isSuccess", false);
        }
        
        // リトライ時に元の問題に戻れるようにIDを渡す
        model.addAttribute("riddleId", id);
        
        // 結果画面は既存のものを使い回す
        return ViewNames.HACKER_RESULT;
    }
}