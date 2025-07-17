document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams({
    sBseDt: '20200601',
    eBseDt: '20200630',
    pageNo: '1',
    numOfRows: '10'
  });

  fetch(`/Tigggle/api/deposit-api-url?${params.toString()}`)
    .then(res => {
      if (!res.ok) throw new Error(`API 오류: ${res.status}`);
      return res.text();
    })
    .then(str => {
      const parser = new DOMParser();
      const xml = parser.parseFromString(str, "application/xml");

      const items = xml.querySelectorAll("response > body > items > item");
      const tbody = document.querySelector("#product-table tbody");
      if (!tbody) {
        console.error("#product-table tbody 요소를 찾을 수 없습니다");
        return;
      }
      tbody.innerHTML = "";

      items.forEach(item => {
        const prdNm = item.querySelector("prdNm")?.textContent ?? "없음";
        const prdOtl = item.querySelector("prdOtl")?.textContent ?? "없음";
        const hitIrt = item.querySelector("hitIrtCndCone")?.textContent ?? "없음";
        const tgt = item.querySelector("jinTgtCone")?.textContent ?? "없음";
        const chn = item.querySelector("prdJinChnCone")?.textContent ?? "없음";
        const trm = item.querySelector("prdJinTrmCone")?.textContent ?? "없음";

        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${prdNm}</td>
          <td>${prdOtl}</td>
          <td>${hitIrt}</td>
          <td>${tgt}</td>
          <td>${chn}</td>
          <td>${trm}</td>
        `;
        tbody.appendChild(row);
      });

      // ✅ 메시지 알림 처리 (이 위치가 맞습니다)
      const message = document.querySelector("#flash-message");
      const error = document.querySelector("#flash-error");

      if (message && message.textContent.trim()) {
        alert(message.textContent.trim());
      }
      if (error && error.textContent.trim()) {
        alert("오류: " + error.textContent.trim());
      }
    })
    .catch(err => {
      console.error("API 오류 발생", err);
    });
});

// 직접 등록한 상품 삭제
function deleteProduct(button) {
  const productId = button.getAttribute('data-id');
  if (!productId) return;

  if (!confirm("정말 이 상품을 삭제하시겠습니까?")) return;

  // 삭제 대상 요소들을 먼저 저장
  const productRow = button.closest('tr');
  const tbody = productRow?.parentElement;
  const manualTable = tbody?.closest('.manual-product-table');
  const bankBlock = manualTable?.closest('.bank-block');

  fetch(`/admin/product/delete/${productId}`, {
    method: 'DELETE',
    headers: {
      'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content,
      'Content-Type': 'application/json'
    }
  })
  .then(res => {
    if (!res.ok) throw new Error("삭제 실패");
    alert("상품이 삭제되었습니다.");

    // 이제 삭제
    productRow.remove();

    // 상품이 모두 삭제되었는지 확인 후 bankBlock 제거
    if (tbody && tbody.querySelectorAll('tr').length === 0 && bankBlock) {
      bankBlock.remove();
    }
  })
  .catch(err => {
    alert("삭제 중 오류가 발생했습니다.");
    console.error(err);
  });
}

function scrollToBank() {
    const input = document.getElementById("bank-search-input").value.trim();
    if (!input) return;

    const normalizedInput = input.replace(/\s/g, '').toLowerCase();

    const target = Array.from(document.querySelectorAll("[id^='bank-']"))
        .find(el => el.id.replace(/\s/g, '').toLowerCase().includes(normalizedInput));

    if (target) {
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
    } else {
        alert("해당 은행을 찾을 수 없습니다.");
    }
}

