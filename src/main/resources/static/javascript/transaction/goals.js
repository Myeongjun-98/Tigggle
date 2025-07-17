// goal-popup.js

// CSRF 토큰을 전역 변수로 설정하여 모든 AJAX 요청에 사용
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");

// --- DOM 요소 변수 선언 ---
const addGoalBtn = document.getElementById('add-goal-btn');
const goalPopupCloseBtn = document.getElementById('goal-popup-close-btn');
const goalTableBody = document.getElementById('goal-table-body');
const selectAllGoalsCheckbox = document.getElementById('select-all-goals');
const deleteSelectedGoalsBtn = document.getElementById('delete-selected-goals-btn');

// 모달 관련 요소
const goalModal = document.getElementById('goal-modal');
const goalModalTitle = document.getElementById('goal-modal-title');
const goalModalCloseBtn = document.getElementById('goal-modal-close-btn');
const goalForm = document.getElementById('goal-form');

// --- 함수: 목표 목록 불러와서 테이블에 렌더링 ---
const fetchAndRenderGoals = async () => {
    try {
        // 1. 서버로부터 목표 목록 데이터를 가져옵니다 (API URL은 예시입니다).
        const response = await fetch('/Tigggle/transaction/goals/api'); // 🚨 백엔드에 이 API를 만들어야 합니다.
        if (!response.ok) throw new Error('데이터를 불러오는데 실패했습니다.');
        const goals = await response.json();

        // 2. 테이블 내용을 비웁니다.
        goalTableBody.innerHTML = '';

        // 3. 데이터가 없으면 메시지를 표시합니다.
        if (goals.length === 0) {
            goalTableBody.innerHTML = '<tr><td colspan="7">등록된 목표가 없습니다.</td></tr>';
            return;
        }

        // 4. 각 목표 데이터를 테이블의 행(tr)으로 만들어 추가합니다.
        let rowNum = 1;
        goals.forEach(goal => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><input type="checkbox" class="goal-checkbox" data-id="${goal.id}"></td>
                <td></td>
                <td>${goal.description}</td>
                <td>${goal.keyword}</td>
                <td>${goal.amount.toLocaleString()}원</td>
                <td>${goal.note || ''}</td>
                <td>
                    <button class="edit-btn" data-id="${goal.id}">수정</button>
                </td>
            `;
            goalTableBody.appendChild(tr);
        });
    } catch (error) {
        console.error(error);
        goalTableBody.innerHTML = `<tr><td colspan="7">${error.message}</td></tr>`;
    }
};

// --- 함수: 모달 열기 ---
const openCreateModal = () => {
    goalModalTitle.textContent = '새로운 목표 추가';
    goalForm.reset(); // 폼 초기화
    document.getElementById('goal-id').value = ''; // 숨겨진 id 필드 비우기
    goalModal.classList.remove('TR-hidden');
};

const openEditModal = async (id) => {
    try {
        const response = await fetch(`/Tigggle/transaction/goals/api/${id}`); // 🚨 백엔드에 이 API를 만들어야 합니다.
        if (!response.ok) throw new Error('목표 정보를 불러올 수 없습니다.');
        const goal = await response.json();

        goalModalTitle.textContent = '목표 수정';
        document.getElementById('goal-id').value = goal.id;
        document.getElementById('goal-description').value = goal.description;
        document.getElementById('goal-keyword').value = goal.keyword.id;
        document.getElementById('goal-amount').value = goal.amount;
        document.getElementById('goal-note').value = goal.note;

        goalModal.classList.remove('TR-hidden');
    } catch (error) {
        alert(error.message);
    }
};

// --- 함수: 모달 닫기 ---
const closeModal = () => {
    goalModal.classList.add('TR-hidden');
};

// --- 이벤트 리스너 설정 ---

// "새로운 목표 추가" 버튼 클릭 시
addGoalBtn.addEventListener('click', openCreateModal);

// 모달의 "닫기" 버튼 클릭 시
goalModalCloseBtn.addEventListener('click', closeModal);

// 팝업 전체의 "닫기" 버튼 클릭 시 (팝업 컨테이너를 숨기는 로직 필요)
// goalPopupCloseBtn.addEventListener('click', () => { ... });

// 폼 제출 (저장) 시
goalForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('goal-id').value;
    const isEditMode = id !== '';

    const goalData = {
        description: document.getElementById('goal-description').value,
        keywordId: document.getElementById('goal-keyword').value,
        amount: document.getElementById('goal-amount').value,
        note: document.getElementById('goal-note').value,
    };

    const url = isEditMode ? `/Tigggle/transaction/goals/api/${id}` : '/Tigggle/transaction/goals/api';
    const method = isEditMode ? 'PUT' : 'POST'; // 🚨 PUT, POST API를 만들어야 합니다.

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                [header]: token // CSRF 토큰 헤더에 추가
            },
            body: JSON.stringify(goalData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || '저장에 실패했습니다.');
        }

        alert(isEditMode ? '성공적으로 수정되었습니다.' : '새로운 목표가 추가되었습니다.');
        closeModal();
        fetchAndRenderGoals(); // 목록 새로고침
    } catch (error) {
        alert(error.message);
    }
});

// 테이블 내부의 '수정' 버튼 클릭 시 (이벤트 위임)
goalTableBody.addEventListener('click', (e) => {
    if (e.target.classList.contains('edit-btn')) {
        const goalId = e.target.dataset.id;
        openEditModal(goalId);
    }
});

// "선택 항목 삭제" 버튼 클릭 시
deleteSelectedGoalsBtn.addEventListener('click', async () => {
    const checkedCheckboxes = document.querySelectorAll('.goal-checkbox:checked');
    if (checkedCheckboxes.length === 0) {
        alert('삭제할 항목을 선택해주세요.');
        return;
    }

    if (!confirm(`선택한 ${checkedCheckboxes.length}개의 목표를 정말 삭제하시겠습니까?`)) {
        return;
    }

    const idsToDelete = Array.from(checkedCheckboxes).map(cb => cb.dataset.id);

    try {
        const response = await fetch('/Tigggle/transaction/goals/api', { // 🚨 DELETE API를 만들어야 합니다.
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(idsToDelete)
        });

        if (!response.ok) throw new Error('삭제에 실패했습니다.');

        alert('성공적으로 삭제되었습니다.');
        fetchAndRenderGoals();
    } catch (error) {
        alert(error.message);
    }
});

document.addEventListener('DOMContentLoaded', () => {
    // '목표 관리' 팝업 페이지가 로드되자마자 목표 목록을 불러옵니다.
    fetchAndRenderGoals();

    const mainCloseButton = document.getElementById('goal-popup-close-btn');

    // 버튼이 존재하면 클릭 이벤트를 추가합니다.
    if (mainCloseButton) {
        mainCloseButton.addEventListener('click', () => {
            // window.close() 함수로 현재 팝업 창을 닫습니다.
            window.close();
        });
    }

});
