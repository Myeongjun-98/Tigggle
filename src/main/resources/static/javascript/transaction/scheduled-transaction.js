// scheduled-transaction.js

let currentEditingScheduleId = null;
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
// 페이지가 로드되면 정기 거래 목록을 불러옵니다.
document.addEventListener('DOMContentLoaded', () => {
    loadScheduledTransactions();
    initializeScheduleModal();
    initializePageButtons();
});

function initializePageButtons() {
    const openModalBtn = document.getElementById('add-schedule-btn');
    const closeModalBtn = document.getElementById('schedule-modal-close-btn');
    const closePopupBtn = document.querySelector('.close-popup-btn');
    const modal = document.getElementById('schedule-modal');

    // '새로운 내역 추가' 버튼 클릭 시 모달 열기
    if (openModalBtn) {
        openModalBtn.addEventListener('click', () => {
            resetAndPrepareCreateForm();
            populateAssetDropdown(); // 자산 목록을 불러옵니다.
            modal.classList.remove('TR-hidden');
        });
    }

    const deleteSelectedBtn = document.getElementById('delete-selected-schedules-btn');
    if (deleteSelectedBtn) {
        deleteSelectedBtn.addEventListener('click', () => {
            // 1. 테이블에서 현재 체크된 모든 체크박스를 찾습니다.
            const checkedBoxes = document.querySelectorAll('.schedule-checkbox:checked');

            if (checkedBoxes.length === 0) {
                alert("삭제할 항목을 선택해주세요.");
                return;
            }

            // 2. 체크된 항목들의 value(ID)를 모아 배열로 만듭니다.
            const idsToDelete = Array.from(checkedBoxes).map(box => box.value);

            // 3. 사용자에게 최종 확인을 받습니다.
            if (confirm(`${idsToDelete.length}개의 정기 거래를 정말 삭제하시겠습니까?`)) {
                // 4. 백엔드 API를 호출하는 함수를 실행합니다.
                deleteScheduledTransactions(idsToDelete);
            }
        });
    }

    // 닫기 버튼 클릭 시 모달 닫기
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => {
            modal.classList.add('TR-hidden');
            resetAndPrepareCreateForm();
        });
    }

    // '팝업 닫기' 버튼 클릭 시
    if (closePopupBtn) {
        closePopupBtn.addEventListener('click', () => window.close());
    }

    if (closePopupBtn) {
        closePopupBtn.addEventListener('click', () => {
            window.close();
        });
    }

    const selectAllCheckbox = document.getElementById('select-all-schedules');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', (event) => {
            // 1. 현재 테이블에 있는 모든 개별 체크박스들을 찾습니다.
            const itemCheckboxes = document.querySelectorAll('.schedule-checkbox');

            // 2. '전체 선택' 체크박스의 현재 상태(체크되었는지 여부)를 가져옵니다.
            const isChecked = event.target.checked;

            // 3. 모든 개별 체크박스들의 상태를 '전체 선택' 체크박스의 상태와 동일하게 만듭니다.
            itemCheckboxes.forEach(checkbox => {
                checkbox.checked = isChecked;
            });
        });
    }

}


/**
 * API를 호출하여 정기 거래 목록을 가져오고, 테이블을 렌더링하는 함수
 */
async function loadScheduledTransactions() {
    const tableBody = document.getElementById('schedule-table-body');
    tableBody.innerHTML = '<tr><td colspan="14">목록을 불러오는 중...</td></tr>';

    try {
        const response = await fetch('/api/scheduled-transactions');
        if (!response.ok) {
            throw new Error('정기 거래 목록을 불러오는데 실패했습니다.');
        }

        const schedules = await response.json();

        // 테이블 내용을 비웁니다.
        tableBody.innerHTML = '';

        if (schedules.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="14">등록된 정기 거래 내역이 없습니다.</td></tr>';
            return;
        }

        // 받아온 데이터로 테이블의 각 행(row)을 만듭니다.
        schedules.forEach(schedule => {

            const row = document.createElement('tr');
            row.innerHTML = `
                <td><input type="checkbox" class="schedule-checkbox" value="${schedule.id}"></td>
                <td>${schedule.description}</td>
                <td class="${schedule.type === '지출' ? 'expense' : 'income'}">${schedule.amount.toLocaleString()}원</td>
                <td>${schedule.keyword}</td>
                <td>${convertFrequencyKo(schedule.frequency)}</td> 
                <td>${schedule.assetAlias}</td>
                <td>${schedule.dayOfExecution}일/요일</td>
                <td>${schedule.nextExecutionDate}</td>
<!--                <td>${schedule.endDate}</td>-->
                ${schedule.endDate === null
                ? `<td>없음</td>`
                : schedule.endDate}
                <td>${schedule.note}</td>
                ${schedule.checkingReflection
                ? `<td><span class="material-symbols-outlined" style="color: red">check</span></td>`
                : `<td><span class="material-symbols-outlined">check_indeterminate_small</span></td>`
            }
                ${schedule.isActive
                ? `<td><span class="material-symbols-outlined" style="color: red">check</span></td>`
                : `<td><span class="material-symbols-outlined">check_indeterminate_small</span></td>`
            }
                <td><button class="edit-btn" data-id="${schedule.id}">수정</button></td>
            `;
            tableBody.appendChild(row);

            const editBtn = row.querySelector('.edit-btn');
            if (editBtn) {
                editBtn.addEventListener('click', () => {
                    openScheduleModalInEditMode(schedule.id);
                });
            }
        });

    } catch (error) {
        console.error(error);
        tableBody.innerHTML = `<tr><td colspan="7">${error.message}</td></tr>`;
    }
}
function initializeScheduleModal() {

    const form = document.getElementById('schedule-create-form');
    const frequencySelect = document.getElementById('schedule-frequency');
    const dayExecutionInput = document.getElementById('schedule-day-execution');
    const dayExecutionLabel = dayExecutionInput.nextElementSibling; // input 바로 뒤의 span 태그

    //'반복 주기' 변경 시, '실행일' 안내 문구 동적 변경
    if (frequencySelect) {
        frequencySelect.addEventListener('change', () => {
            const selectedFrequency = frequencySelect.value;
            if (selectedFrequency === 'MONTHLY') {
                dayExecutionLabel.textContent = '(매월: 1-31일)';
                dayExecutionInput.max = 31;
            } else if (selectedFrequency === 'WEEKLY') {
                dayExecutionLabel.textContent = '(매주: 월=1, 화=2, ..., 일=7)';
                dayExecutionInput.max = 7;
            } else { // YEARLY 등
                dayExecutionLabel.textContent = '(실행일)';
            }
        });
    }


    if(form){
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            // 수정모드 / 생성모드 판별
            const isEditMode = currentEditingScheduleId !== null;
            // 폼 데이터를 기반으로 DTO 객체를 만듭니다.
            const dto = createDtoFromForm();
            const url = isEditMode ? `/api/scheduled-transactions/${currentEditingScheduleId}` : '/api/scheduled-transactions';
            const method = isEditMode ? 'PATCH' : 'POST';
            // fetch API를 사용하여 백엔드로 데이터를 전송합니다.
            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json',
                        [header]: token,
                    },
                    body: JSON.stringify(dto) // JavaScript 객체를 JSON 문자열로 변환
                });

                if (response.ok) {
                    const message = isEditMode ? '성공적으로 수정되었습니다.' : '성공적으로 등록되었습니다.';
                    alert(message);
                    document.getElementById('schedule-modal').classList.add('TR-hidden');
                    loadScheduledTransactions(); // 목록을 새로고침합니다.
                    currentEditingScheduleId = null; // 수정 모드 종료!
                    resetAndPrepareCreateForm();
                } else {
                    const errorText = await response.text();
                    alert(`저장에 실패했습니다: ${errorText}`);
                }
            } catch (error) {
                console.error('API 호출 중 오류 발생:', error);
                alert('저장 중 오류가 발생했습니다.');
            } finally {
                // 4. 작업이 끝나면 항상 '수정 모드'를 해제합니다.
                currentEditingScheduleId = null;
            }
        });
    }
}

//* 폼 데이터를 기반으로 서버에 보낼 DTO 객체를 생성합니다.
function createDtoFromForm() {
    const form = document.getElementById('schedule-create-form');
    return {
        assetId: form.querySelector('#schedule-asset').value,
        description: form.querySelector('#schedule-description').value,
        amount: form.querySelector('#schedule-amount').value,
        isConsumption: form.querySelector('input[name="scheduleType"]:checked').value === 'EXPENSE',
        payMethod: 'SCHEDULED',
        keywordId: form.querySelector('#schedule-keyword').value,
        note: form.querySelector('#schedule-note').value,
        frequency: form.querySelector('#schedule-frequency').value,
        dayOfExecution: form.querySelector('#schedule-day-execution').value,
        // startDate: form.querySelector('#schedule-start-date').value,
        endDate: form.querySelector('#schedule-end-date').value || null,
        reflectOnAsset: form.querySelector('#schedule-reflect-asset').checked
    };
}


// * API를 호출하여 모달의 '자산' 드롭다운 목록을 채우는 함수
async function populateAssetDropdown() {
    const assetSelect = document.getElementById('schedule-asset');
    assetSelect.innerHTML = '<option value="">자산을 불러오는 중...</option>';

    try {
        const response = await fetch('/api/transactions/assets-for-schedule'); // 이 API 주소는 실제 프로젝트에 맞게 확인 필요
        if (!response.ok) throw new Error('자산 목록 로딩 실패');

        const assets = await response.json();

        assetSelect.innerHTML = ''; // 기존 내용 비우기
        const defaultOption = document.createElement('option');
        defaultOption.value = "";
        defaultOption.textContent = "=== 자산 선택 ===";
        assetSelect.appendChild(defaultOption);

        assets.forEach(asset => {
            const option = document.createElement('option');
            option.value = asset.id;
            option.textContent = `[${asset.type}] ${asset.alias}`;
            assetSelect.appendChild(option);
        });

    } catch (error) {
        console.error(error);
        assetSelect.innerHTML = '<option value="">자산을 불러올 수 없습니다.</option>';
    }
}

// * ID 배열을 받아, 일괄 삭제 API를 호출하는 함수
// * @param {Array<string>} ids - 삭제할 정기 거래 ID의 배열
async function deleteScheduledTransactions(ids) {

    try {
        const response = await fetch('/api/scheduled-transactions', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                [header]: token,
            },
            body: JSON.stringify(ids) // ID 배열을 JSON 문자열로 변환하여 전송
        });

        if (response.ok) {
            alert('선택한 항목이 성공적으로 삭제되었습니다.');
            loadScheduledTransactions(); // 목록을 새로고침하여 삭제된 내역을 반영합니다.
            document.getElementById('select-all-schedules').checked = false;
        } else {
            const errorText = await response.text();
            alert(`삭제에 실패했습니다: ${errorText}`);
        }
    } catch (error) {
        console.error("정기 거래 삭제 중 오류:", error);
        alert('삭제 중 오류가 발생했습니다.');
    }
}

// * '수정 모드'로 정기 거래 모달을 열고 데이터를 채우는 함수
// * @param {number} scheduleId - 수정할 정기 거래의 ID
async function openScheduleModalInEditMode(scheduleId) {
    if (!scheduleId) return;

    try {
        const response = await fetch(`/api/scheduled-transactions/${scheduleId}`);
    const scheduleData = await response.json();

    const modal = document.getElementById('schedule-modal');

    // 2. '수정 중인 ID'를 전역 변수에 저장하여 '수정 모드'임을 알립니다.
    currentEditingScheduleId = scheduleId;

    // 3. 모달 제목과 버튼 텍스트를 변경합니다.
    modal.querySelector('h2').innerText = '정기 거래 수정';
    modal.querySelector('.submit-button').innerText = '수정하기';

    // 4. 가져온 데이터로 폼 필드를 채웁니다.
    const form = document.getElementById('schedule-create-form');
    form.querySelector(`input[name="scheduleType"][value="${scheduleData.isConsumption ? 'EXPENSE' : 'INCOME'}"]`).checked = true;
    form.querySelector('#schedule-frequency').value = scheduleData.frequency;
    form.querySelector('#schedule-day-execution').value = scheduleData.dayOfExecution;
    await populateAssetDropdown();
    form.querySelector('#schedule-asset').value = scheduleData.assetId;
    form.querySelector('#schedule-description').value = scheduleData.description;
    form.querySelector('#schedule-amount').value = scheduleData.amount;
    form.querySelector('#schedule-start-date').value = scheduleData.startDate;
    form.querySelector('#schedule-end-date').value = scheduleData.endDate;
    form.querySelector('#schedule-keyword').value = scheduleData.keywordId;
    form.querySelector('#schedule-note').value = scheduleData.note;
    form.querySelector('#schedule-reflect-asset').checked = scheduleData.reflectOnAsset;

    // 5. 모달을 엽니다.
    modal.classList.remove('TR-hidden');
    }catch (error) {
        alert('수정할 내역을 불러오는 데 실패했습니다.')
    }
}

async function toggleEditMode(scheduleId) {
    const row = document.getElementById(`schedule-row-${scheduleId}`);
    const editBtn = row.querySelector('.edit-btn');
    const isEditing = editBtn.textContent === '저장';

    if (isEditing) {
        // --- 저장 모드일 때 (API 호출) ---
        const updateDto = {
            description: row.querySelector('[name="description"]').value,
            amount: row.querySelector('[name="amount"]').value,
            // ... 다른 input 필드들의 값을 가져옵니다 ...
        };

        // PATCH API 호출
        const response = await fetch(`/api/scheduled-transactions/${scheduleId}`, { /* ... */ });

        if (response.ok) {
            alert('수정되었습니다.');
            loadScheduledTransactions(); // 성공 시 목록 새로고침
        } else {
            alert('수정에 실패했습니다.');
        }

    } else {
        // --- 수정 모드로 전환 ---
        // 1. 상세 데이터를 가져옵니다. (기존 모달에서 사용하던 API 재활용)
        const response = await fetch(`/api/scheduled-transactions/${scheduleId}`);
        const data = await response.json();

        // 2. 텍스트들을 input으로 바꿉니다.
        row.querySelector('[data-field="description"]').innerHTML = `<input name="description" type="text" value="${data.description}">`;
        row.querySelector('[data-field="amount"]').innerHTML = `<input name="amount" type="number" value="${data.amount}">`;
        // ... 다른 필드들도 input으로 변경 ...

        editBtn.textContent = '저장';
        editBtn.style.backgroundColor = '#007bff'; // 색상 변경
    }
}

// '생성 모드'로 모달을 여는 함수
function openScheduleModalInCreateMode() {
    currentEditingScheduleId = null; // 생성 모드이므로 ID 초기화
    const modal = document.getElementById('schedule-modal');

    modal.querySelector('h2').innerText = '새로운 정기 거래 추가';
    modal.querySelector('.submit-button').innerText = '저장하기';

    document.getElementById('schedule-create-form').reset(); // 폼 리셋
    populateAssetDropdown(); // 자산 목록 불러오기
    modal.classList.remove('TR-hidden');
}

// * frequency 영어 값을 한글로 변환하는 헬퍼 함수
// * @param {string} frequency - "MONTHLY", "WEEKLY", "YEARLY"
function convertFrequencyKo(frequency) {
    switch(frequency) {
        case "MONTHLY":
            return "매월";
        case "WEEKLY":
            return "매주";
        case "YEARLY":
            return "매년";
        default:
            return frequency; // 알 수 없는 값이면 원본 그대로 반환
    }
}

// * '정기 거래 추가' 모달의 모든 상태를 기본값(생성 모드)으로 초기화하는 함수
function resetAndPrepareCreateForm() {
    // 1. 수정 모드 상태를 확실하게 종료합니다.
    currentEditingScheduleId = null;

    const modal = document.getElementById('schedule-modal');
    const form = document.getElementById('schedule-create-form');

    // 2. 모달의 제목과 버튼 텍스트를 '생성 모드'의 기본값으로 되돌립니다.
    modal.querySelector('h2').innerText = '새로운 정기 거래 추가';
    modal.querySelector('.submit-button').innerText = '저장하기';

    // 3. form.reset()을 호출하여 모든 input, textarea 값을 초기화합니다.
    form.reset();

    // 4. '반복 주기' select를 '매월'로, 안내 문구를 기본값으로 되돌립니다.
    form.querySelector('#schedule-frequency').value = 'MONTHLY';
    form.querySelector('#day-execution-guide').textContent = '(매월: 1-31일)';

    // 5. '자산 총액에 반영' 스위치를 기본값(체크된 상태)으로 되돌립니다.
    form.querySelector('#schedule-reflect-asset').checked = true;
}