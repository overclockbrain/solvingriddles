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
});

/* ==================================================
   ヘルパー関数（グローバルに残すもの）
   ================================================== */

/**
 * マウス位置判定用（Sort機能で使用）
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