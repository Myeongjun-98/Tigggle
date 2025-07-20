// static/javascript/insite/asset.js

document.addEventListener('DOMContentLoaded', () => {

    // --- CSRF 토큰 설정 (AJAX 요청 시 필요) ---
    const token = document.querySelector("meta[name='_csrf']")?.content;
    const header = document.querySelector("meta[name='_csrf_header']")?.content;

    // --- DOM 요소 가져오기 ---
    const addAssetBtn = document.getElementById('add-asset-btn');

    // 메인 모달 요소
    const mainModal = document.getElementById('asset-main-modal');
    const mainForm = document.getElementById('asset-main-form');
    const assetTypeButtons = mainModal.querySelectorAll('.type-button');
    const assetTypeInput = document.getElementById('asset-type-input');
    const aliasInput = document.getElementById('asset-alias-input');
    const balanceInput = document.getElementById('asset-balance-input');
    const mainCancelBtn = document.getElementById('asset-main-cancel-btn');
    const mainNextBtn = document.getElementById('asset-main-next-btn');

    // 은행 모달 요소
    const bankModal = document.getElementById('asset-bank-modal');
    const bankForm = document.getElementById('asset-bank-form');
    const bankSelect = document.getElementById('asset-bank-select');
    const accountNumberInput = document.getElementById('asset-account-number-input');
    const bankCancelBtn = document.getElementById('asset-bank-cancel-btn');
    const bankRegisterBtn = document.getElementById('asset-bank-register-btn');


    // --- 함수: 모든 폼 초기화 ---
    const resetAllForms = () => {
        // 메인 모달 초기화
        mainForm.reset();
        assetTypeInput.value = 'CASH'; // 기본값 '현금'으로
        assetTypeButtons.forEach(btn => {
            if (btn.dataset.value === 'CASH') btn.classList.add('selected');
            else btn.classList.remove('selected');
        });
        mainNextBtn.textContent = '등록'; // 버튼 텍스트 초기화
        mainNextBtn.disabled = true; // 버튼 비활성화

        // 은행 모달 초기화
        bankForm.reset();
        bankRegisterBtn.disabled = true;
    };


    // --- 함수: 메인 폼 유효성 검사 및 버튼 활성화 ---
    const validateMainForm = () => {
        const isAliasValid = aliasInput.value.trim() !== '';
        mainNextBtn.disabled = !isAliasValid;
    };

    // --- 함수: 은행 폼 유효성 검사 및 버튼 활성화 ---
    const validateBankForm = () => {
        const isBankValid = bankSelect.value !== '';
        const isAccountNumberValid = accountNumberInput.value.trim() !== '';
        bankRegisterBtn.disabled = !(isBankValid && isAccountNumberValid);
    };


    // --- 이벤트 리스너 설정 ---

    // '내 자산 추가' 버튼 클릭 시
    addAssetBtn.addEventListener('click', () => {
        resetAllForms();
        mainModal.classList.remove('hidden');
    });

    // 메인 모달 '취소' 버튼 클릭 시
    mainCancelBtn.addEventListener('click', () => {
        mainModal.classList.add('hidden');
    });

    // 은행 모달 '취소' 버튼 클릭 시
    bankCancelBtn.addEventListener('click', () => {
        bankModal.classList.add('hidden');
    });

    // 자산 타입 버튼('현금', '보통예금') 클릭 시
    assetTypeButtons.forEach(button => {
        button.addEventListener('click', () => {
            assetTypeButtons.forEach(btn => btn.classList.remove('selected'));
            button.classList.add('selected');
            const selectedType = button.dataset.value;
            assetTypeInput.value = selectedType;

            // 타입에 따라 버튼 텍스트 변경
            if (selectedType === 'CASH') {
                mainNextBtn.textContent = '등록';
            } else {
                mainNextBtn.textContent = '다음';
            }
        });
    });

    // 메인 모달 입력창에 입력이 발생할 때마다 유효성 검사 실행
    aliasInput.addEventListener('input', validateMainForm);
    balanceInput.addEventListener('input', validateMainForm);

    // 은행 모달 입력창에 입력/변경이 발생할 때마다 유효성 검사 실행
    bankSelect.addEventListener('change', validateBankForm);
    accountNumberInput.addEventListener('input', validateBankForm);


    // 메인 모달 '다음'/'등록' 버튼 클릭 시
    mainNextBtn.addEventListener('click', () => {
        const assetType = assetTypeInput.value;
        const balanceValue = balanceInput.value.trim() === '' ? 0 : balanceInput.value;

        if (assetType === 'CASH') {
            // 현금일 경우, 바로 등록 API 호출
            const assetData = {
                assetType: 'CASH',
                alias: aliasInput.value,
                balance: balanceValue,
            };
            registerAsset(assetData);
        } else {
            // 보통예금일 경우, 은행 모달을 보여줌
            mainModal.classList.add('hidden');
            bankModal.classList.remove('hidden');
            // 은행 목록을 서버에서 불러와 채웁니다.
            fetchBankList();
        }
    });

    // 은행 모달 '등록' 버튼 클릭 시
    bankRegisterBtn.addEventListener('click', () => {
        const balanceValue = balanceInput.value.trim() === '' ? 0 : balanceInput.value;
        const assetData = {
            assetType: 'ORDINARY',
            alias: aliasInput.value,
            balance: balanceValue,
            bankId: bankSelect.value,
            accountNumber: accountNumberInput.value
        };
        registerAsset(assetData);
    });

    // --- AJAX 함수 ---

    // 은행 목록을 서버에서 가져오는 함수
    const fetchBankList = async () => {
        try {
            const response = await fetch('/api/banks');
            const banks = await response.json();
            bankSelect.innerHTML = '<option value="" selected disabled>은행을 선택하세요</option>';
            banks.forEach(bank => {
                const option = document.createElement('option');
                option.value = bank.id;
                option.textContent = bank.name;
                bankSelect.appendChild(option);
            });
        } catch (error) {
            console.error('은행 목록 로딩 실패:', error);
        }
    };

    // 최종 자산 데이터를 서버로 전송하는 함수
    const registerAsset = async (assetData) => {
        try {
            const response = await fetch('/api/assets', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(assetData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || '자산 등록에 실패했습니다.');
            }

            alert('자산이 성공적으로 등록되었습니다.');
            mainModal.classList.add('hidden');
            bankModal.classList.add('hidden');
            window.location.reload(); // 페이지 새로고침하여 자산 목록 업데이트

        } catch (error) {
            alert(error.message);
        }
    };
});