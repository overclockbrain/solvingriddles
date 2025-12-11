package com.example.solvingriddles.controller.casual;

import com.example.solvingriddles.constant.UrlConst;
import com.example.solvingriddles.constant.ViewNames;
import com.example.solvingriddles.service.RiddleService;
import com.example.solvingriddles.model.Riddle;

import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

/**
 * カジュアルモード用コントローラ
 * カジュアルモードのトップ画面表示を担当する
 * 主に初心者向けの簡単な謎解きを提供する画面を表示する
 * URLパスやビュー名は定数クラスで管理し、ハードコーディングを防ぐ
 */
@Controller
@RequestMapping(UrlConst.CASUAL_BASE) // "/casual"
public class CasualController {

    // Serviceを使えるように注入！
    private final RiddleService riddleService;
    private final static String MODE = "CASUAL";

    /**
     * コンストラクタ
     * Springの依存性注入(DI)により、自動的にRiddleServiceが渡される。
     * @param riddleService 謎解きのロジックを担当するサービス
     */
    public CasualController(RiddleService riddleService) {
        this.riddleService = riddleService;
    }

    /**
     * カジュアルモードのトップ画面表示
     * GETリクエストで/casual/indexにアクセスした際に
     * カジュアルモード用のトップ画面テンプレートを返す
     * @return カジュアルモードのトップ画面HTMLファイル名 (casual/index.html)
     */
    @GetMapping(UrlConst.CASUAL_INDEX) // "/index"
    public String index() {
        return ViewNames.CASUAL_INDEX; // "casual/index"
    }

    /**
     * カジュアルモードの謎解き一覧画面表示
     * Service層から全ての謎解きデータを取得し、HTMLに渡す。
     * @param model 画面(HTML)にデータを渡すための入れ物
     * @return 一覧画面のHTMLファイル名 (casual/list.html)
     */
    @GetMapping(UrlConst.CASUAL_LIST) // "/list"
    public String list(Model model) {
        // 全件取得して画面に渡す
        model.addAttribute("riddles", riddleService.findAll(MODE));
        return ViewNames.CASUAL_LIST; // "casual/list"
    }

    /**
     * カジュアルモードのクイズ画面表示
     * 指定されたIDの謎解きデータをService層から取得し、HTMLに渡す。
     * @param id 謎解きのID
     * @param model 画面(HTML)にデータを渡すための入れ物
     * @return クイズ画面のHTMLファイル名 (casual/quiz.html)
     */
    @GetMapping(UrlConst.CASUAL_QUIZ + "/{id}") // "/quiz/{id}"
    public String quiz(@PathVariable("id") Integer id, Model model) {
        // IDで検索して、あれば埋める、なければ...とりあえず今は考えない(nullになるかも)
        Optional<Riddle> riddle = riddleService.findById(MODE, id);
        
        if(riddle.isEmpty()) {
            // 問題が見つからなかった場合は一覧にリダイレクト
            return "redirect:" + UrlConst.CASUAL_BASE + UrlConst.CASUAL_LIST;
        }
        
        // データがある時だけ画面を表示
        model.addAttribute("riddle", riddle.get());
        return ViewNames.CASUAL_QUIZ; // "casual/quiz"
    }

    /**
     * カジュアルモードの答え合わせ処理
     * POSTリクエストで送信された解答をService層で判定し、
     * 結果に応じて結果画面へリダイレクトする。
     * @param id 謎解きのID
     * @param answer ユーザーが入力した解答
     * @return 結果画面へのリダイレクトURL
     */
    @PostMapping(UrlConst.CASUAL_QUIZ_CHECK) // "/quiz/check"
    public String checkAnswer(@RequestParam("id") Integer id, 
                              @RequestParam("answer") String answer) {
        
        // Serviceで正誤判定
        boolean isCorrect = riddleService.checkAnswer(MODE, id, answer);

        // 結果画面へリダイレクト（クエリパラメータで結果を渡す単純な実装）
        return "redirect:" + UrlConst.CASUAL_BASE + "/result?success=" + isCorrect+ "&id=" + id;
    }

    /**
     * カジュアルモードの結果画面表示
     * クエリパラメータで受け取った正誤情報をHTMLに渡し、
     * 結果画面を表示する。
     * @param success 正解ならtrue、不正解ならfalse
     * @param model 画面(HTML)にデータを渡すための入れ物
     * @return 結果画面のHTMLファイル名 (casual/result.html)
     */
    @GetMapping(UrlConst.CASUAL_RESULT) // "/result"
    public String result(@RequestParam("success") boolean success,@RequestParam("id") Integer id, Model model) {
        model.addAttribute("isSuccess", success);
        // リトライ時に元の問題に戻れるようにIDを渡す
        model.addAttribute("riddleId", id);
        return ViewNames.CASUAL_RESULT; // "casual/result"
    }
}