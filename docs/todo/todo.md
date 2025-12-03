# 12/1週のタスク

## 🥅 ゴール1： サーバー構築とアクセス
- [x] GitHubにリポジトリを作る
- [x] Renderのアカウントを作る（レンタルサーバーより設定が楽やから推奨）
- [x] Hello WorldだけのコードをGitHubにプッシュする
- [x] RenderとGitHubを連携して、Webに表示されるか確認する（これがCI/CDの第一歩や！）

## 🥅 ゴール2： アプリの流れと実装
- [x] 画面遷移図をざっくり描く（mdか手書き写真）
- [x] どんな「謎」を出すか1問だけ考える
- [x] Thymeleafでトップ画面（モック）を作る
- [x] Thymeleafで回答画面（モック）を作る

## 🥅 ゴール3: 謎解き機能の構造化とTDD実践
業務レベルの「3層アーキテクチャ」を導入し、Service層の実装を**TDD（テスト駆動開発）**で行う。

- [x] **Step 1: データの定義 (Model)**
    - [x] `src/main/java/com/example/solvingriddles/model/Riddle.java` を作成
    - [x] 項目: ID, 問題文, 正解, ヒント

- [x] **Step 2: 倉庫番の作成 (Repository)**
    - [x] `src/main/java/com/example/solvingriddles/repository/RiddleRepository.java` を作成
    - [x] `Map` を使って、メモリ上でデータを管理する
    - [x] `findById(int id)` メソッドを用意

- [x] **Step 3: 頭脳の作成 (Service) ★TDDチャレンジ**
    - [x] **テスト作成 (Red):** `src/test/java/.../service/RiddleServiceTest.java` を作成
        - [x] 「正解の文字（"t"）を渡したら `true` が返る」テストを書く
        - [x] 「不正解の文字（"a"）を渡したら `false` が返る」テストを書く
        - [x] 実行して**赤色（失敗）**になることを確認する
    - [x] **実装 (Green):** `RiddleService.java` を作成し、テストが通る最小限のコードを書く
    - [x] **リファクタリング (Refactor):** コードを整理して、もう一度テストを通す

- [x] **Step 4: コントローラーの改修**
    - [x] `MainController` を修正し、作成した `Service` を使うように変更
    - [x] URLを `/quiz/{id}` に対応させる

## 🥅 ゴール4: 画面の共通化とメニュー実装
全画面のレイアウトを統一し、スマホ対応のメニューを作る。

- [ ] **Step 1: 共通部品の作成 (Fragments)**
    - [ ] `src/main/resources/templates/common/head.html` (CSSなど)
    - [ ] `src/main/resources/templates/common/header.html` (ナビバー)
    - [ ] `src/main/resources/templates/common/layout.html` (枠組み)

- [ ] **Step 2: 各画面への適用**
    - [ ] `index.html`, `list.html`, `quiz.html`, `result.html` を修正
    - [ ] `th:replace` を使って共通部品を読み込む

- [ ] **Step 3: アコーディオンメニューの実装**
    - [ ] ハンバーガーボタン（≡）をヘッダーに追加
    - [ ] `static/js/script.js` を作成し、クリックでの開閉処理を記述

---

## 🛡️ TDD・勉強
- [ ] JUnit（テストツール）が動くか試してみる
- [ ] 「足し算」くらいの簡単なロジックでテストを1個書いてみる

## ☕ その他（超重要）
- [ ] 疲れたらすぐ寝る
- [ ] できたことを褒める