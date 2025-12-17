document.addEventListener('DOMContentLoaded', function () {

    /* ==================================================
       1. ハンバーガーメニューの制御
       ================================================== */
    const menuBtn = document.getElementById('menu-btn');
    const sideMenu = document.getElementById('side-menu');

    if (menuBtn && sideMenu) {
        menuBtn.addEventListener('click', function () {
            if (sideMenu.style.display === 'none' || sideMenu.style.display === '') {
                sideMenu.style.display = 'block';
                menuBtn.textContent = '×';
            } else {
                sideMenu.style.display = 'none';
                menuBtn.textContent = '≡';
            }
        });
    }

    /* ==================================================
       2. 並べ替え問題 (Sort) のドラッグ＆ドロップ
       ================================================== */
    const sortList = document.getElementById("sortable-list");

    if (sortList) {
        let draggingItem = null;

        sortList.addEventListener("dragstart", (e) => {
            draggingItem = e.target;
            setTimeout(() => e.target.classList.add("dragging"), 0);
        });

        sortList.addEventListener("dragend", (e) => {
            e.target.classList.remove("dragging");
            draggingItem = null;
        });

        sortList.addEventListener("dragover", (e) => {
            e.preventDefault();
            const afterElement = getDragAfterElement(sortList, e.clientY);
            if (afterElement == null) {
                sortList.appendChild(draggingItem);
            } else {
                sortList.insertBefore(draggingItem, afterElement);
            }
        });
    }

    /* ==================================================
       3. 暴走回答欄 (Moving / KAN-25) の制御
       ================================================== */
    const toggleBtn = document.getElementById('toggleButton');

    // ★ここが修正ポイント！ボタンがある時だけ動くからエラー出へん
    if (toggleBtn) {
        toggleBtn.addEventListener('click', function () {
            const target = document.getElementById('movingForm');

            if (target) {
                // アニメーション停止/再開
                target.classList.toggle('paused');

                // ボタンの見た目切り替え
                if (target.classList.contains('paused')) {
                    toggleBtn.innerHTML = '🏃‍♂️ 再開する！！';
                    toggleBtn.classList.remove('btn-danger');
                    toggleBtn.classList.add('btn-success');
                } else {
                    toggleBtn.innerHTML = '🛑 止まれ！！';
                    toggleBtn.classList.remove('btn-success');
                    toggleBtn.classList.add('btn-danger');
                }
            }
        });
    }

    /* ==================================================
       4. トグルスイッチ (Toggle / KAN-20) の制御
       ================================================== */
    const switches = document.querySelectorAll('.toggle-switch');
    const bulb = document.getElementById('bulbIcon');
    const statusText = document.querySelector('.toggle-status');

    // ★ここ変更: 特定のdivじゃなくて、body全体を操作対象にする
    // const section = document.querySelector('.toggle-section'); ←これはもう要らん

    // section のチェックを外して、switchesなどのチェックだけにする
    if (switches.length > 0 && bulb && statusText) {

        function checkAllSwitches() {
            const checkedCount = document.querySelectorAll('.toggle-switch:checked').length;

            // ★ここ変更: bodyタグに属性をつける！
            document.body.setAttribute('data-brightness', checkedCount);

            if (checkedCount === switches.length) {
                // 全点灯
                bulb.classList.add('on');
                statusText.textContent = "電源復旧！明るくなった！";
                statusText.style.color = "#f57f17";
            } else {
                // まだ暗い
                bulb.classList.remove('on');
                statusText.textContent = "まだ暗いな... ブレーカー上げな...";
                statusText.style.color = "";
            }
        }

        switches.forEach(sw => {
            sw.addEventListener('change', checkAllSwitches);
        });

        // 初期実行（これでページ開いた瞬間に body が暗くなる）
        checkAllSwitches();
    }

    /* ==================================================
       5. Enterキー & スマホボタン長押し (Longpress / Q12)
       ================================================== */
    const chargeBar = document.getElementById('chargeBar');
    const chargeText = document.getElementById('chargeText');
    const hiddenInput = document.getElementById('hiddenAnswerInput');
    const quizForm = document.querySelector('form');
    // スマホ用ボタン
    const mobileBtn = document.getElementById('mobileChargeBtn');

    if (chargeBar && hiddenInput && quizForm) {

        let chargeLevel = 0;
        let isCharging = false;
        let chargeInterval;

        // --- 共通: チャージ開始処理 ---
        function startCharge() {
            if (!isCharging && chargeLevel < 100) {
                isCharging = true;
                chargeBar.classList.add('charging');
                if (mobileBtn) mobileBtn.classList.add('active'); // ボタン凹ませる

                chargeInterval = setInterval(() => {
                    chargeLevel += 1.5;
                    chargeBar.style.width = chargeLevel + '%';
                    chargeText.textContent = Math.floor(chargeLevel) + '%';

                    if (chargeLevel >= 100) {
                        stopCharge(); // タイマー止める
                        completeCharge();
                    }
                }, 30);
            }
        }

        // --- 共通: チャージ停止処理 ---
        function stopCharge() {
            isCharging = false;
            clearInterval(chargeInterval);
            chargeBar.classList.remove('charging');
            if (mobileBtn) mobileBtn.classList.remove('active');

            // 満タンじゃなかったらリセット
            if (chargeLevel < 100) {
                chargeLevel = 0;
                chargeBar.style.width = '0%';
                chargeBar.style.backgroundColor = '';
                chargeText.textContent = '0%';
            }
        }

        // --- キーボードイベント ---
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                startCharge();
            }
        });
        document.addEventListener('keyup', (e) => {
            if (e.key === 'Enter') stopCharge();
        });

        // --- ★スマホ用タッチイベント (ボタンがあれば) ---
        if (mobileBtn) {
            mobileBtn.addEventListener('touchstart', (e) => {
                e.preventDefault(); // メニューとか出さんように
                startCharge();
            });
            mobileBtn.addEventListener('touchend', (e) => {
                e.preventDefault();
                stopCharge();
            });
            // 指がボタンから外れた時も停止
            mobileBtn.addEventListener('touchleave', stopCharge);
        }

        function completeCharge() {
            chargeBar.classList.add('charged-full');
            chargeText.textContent = 'MAX!!';
            hiddenInput.value = 'CHARGE_COMPLETE';
            setTimeout(() => { quizForm.submit(); }, 500);
        }
    }

    /* ==================================================
       6. 2キー同時長押し & マルチタップ (Dual Longpress / Q13)
       ================================================== */
    const dualChargeBar = document.getElementById('dualChargeBar');
    const dualChargeText = document.getElementById('dualChargeText');
    const keyC = document.getElementById('keyC');
    const keyEnter = document.getElementById('keyEnter');
    const quizFormDual = document.querySelector('form');
    const realInputDual = document.querySelector('input[name="answer"]');

    if (dualChargeBar && quizFormDual && realInputDual) {
        realInputDual.style.display = 'none';
        const submitBtn = document.querySelector('button[type="submit"]');
        if (submitBtn) submitBtn.style.display = 'none';

        let dualChargeLevel = 0;
        let dualInterval;
        let isDualCharging = false;

        // 入力状態管理 (キーボードもタッチもここに集約)
        const activeInputs = new Set();

        // --- 状態更新関数 ---
        function updateDualState() {
            // C と Enter が両方アクティブか？
            const hasC = activeInputs.has('c');
            const hasEnter = activeInputs.has('enter');

            // 見た目の更新 (キーボードでもタッチでも光らせる)
            if (hasC) keyC.classList.add('key-active'); else keyC.classList.remove('key-active');
            if (hasEnter) keyEnter.classList.add('key-active'); else keyEnter.classList.remove('key-active');

            // チャージ判定
            if (hasC && hasEnter) {
                if (!isDualCharging && dualChargeLevel < 100) {
                    startDualCharge();
                }
            } else {
                if (isDualCharging) {
                    stopDualCharge();
                }
            }
        }

        function startDualCharge() {
            isDualCharging = true;
            dualChargeBar.classList.add('charging');
            dualInterval = setInterval(() => {
                dualChargeLevel += 2.0;
                dualChargeBar.style.width = dualChargeLevel + '%';
                dualChargeText.textContent = Math.floor(dualChargeLevel) + '%';
                if (dualChargeLevel >= 100) {
                    clearInterval(dualInterval);
                    completeDualCharge();
                }
            }, 30);
        }

        function stopDualCharge() {
            isDualCharging = false;
            clearInterval(dualInterval);
            dualChargeBar.classList.remove('charging');
            // リセット
            if (dualChargeLevel < 100) {
                dualChargeLevel = 0;
                dualChargeBar.style.width = '0%';
                dualChargeText.textContent = '0%';
            }
        }

        // --- キーボードイベント ---
        document.addEventListener('keydown', (e) => {
            activeInputs.add(e.key.toLowerCase());
            updateDualState();
        });
        document.addEventListener('keyup', (e) => {
            activeInputs.delete(e.key.toLowerCase());
            updateDualState();
        });

        // --- ★スマホ用タッチイベント ---
        // ヘルパー関数: タッチ登録
        function addTouchListener(elem, keyName) {
            if (!elem) return;
            elem.addEventListener('touchstart', (e) => {
                e.preventDefault(); // 拡大とか防ぐ
                activeInputs.add(keyName);
                updateDualState();
            });
            elem.addEventListener('touchend', (e) => {
                e.preventDefault();
                activeInputs.delete(keyName);
                updateDualState();
            });
            // 指が外れたらOFFにする
            elem.addEventListener('touchleave', (e) => {
                activeInputs.delete(keyName);
                updateDualState();
            });
        }

        // 画面上のアイコンをボタン化する
        addTouchListener(keyC, 'c');
        addTouchListener(keyEnter, 'enter');


        function completeDualCharge() {
            dualChargeBar.classList.remove('charging');
            dualChargeBar.classList.add('charged-full');
            dualChargeText.textContent = 'REBOOT!!';
            realInputDual.value = 'DUAL_CHARGE_COMPLETE';
            setTimeout(() => { quizFormDual.submit(); }, 600);
        }
    }

    /* ==================================================
       KAN-33. 電球パズル (Lights / Q14)
       ================================================== */
    (function () {
        const bulbs = document.querySelectorAll('.light-bulb-btn');
        const hiddenInput = document.getElementById('lightsAnswerInput');

        if (bulbs.length > 0 && hiddenInput) {
            let states = [0, 0, 0];
            bulbs.forEach((bulb, index) => {
                bulb.addEventListener('click', () => {
                    states[index] = states[index] === 0 ? 1 : 0;
                    if (states[index] === 1) {
                        bulb.classList.add('on');
                    } else {
                        bulb.classList.remove('on');
                    }
                    hiddenInput.value = states.join('');
                });
            });
        }
    })();
});

/* ==================================================
   ヘルパー関数（グローバルに残すもの）
   ================================================== */

/**
 * クリックされた内容を送信する関数
 * @param {*} val 
 */
function submitAnswer(val) {
    document.getElementById('hiddenAnswer').value = val;
    document.getElementById('quizForm').submit();
}

/**
 * マウス位置判定用（Sort機能で使用）
 * @param {HTMLElement} container 
 * @param {number} y 
 * @returns {HTMLElement|null}
 */
function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll(".sort-item:not(.dragging)")];

    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;

        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}

/**
 * 並べ替え送信（HTMLのonclickから呼ぶならこれが必要）
 * ※もしHTML側も addEventListener に変えるなら、これも中に入れられるで
 */
function submitSortAnswer() {
    const listItems = document.querySelectorAll(".sort-item");
    if (listItems.length === 0) return; // エラー防止

    const answerArray = Array.from(listItems).map(item => item.getAttribute("data-value"));
    const finalAnswer = answerArray.join(",");

    const hiddenInput = document.getElementById("hiddenAnswer");
    const sortForm = document.getElementById("sortForm");

    if (hiddenInput && sortForm) {
        hiddenInput.value = finalAnswer;
        sortForm.submit();
    }
}