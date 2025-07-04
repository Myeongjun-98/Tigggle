document.addEventListener("DOMContentLoaded", () => {
  let selectedFiles = [];

  // 이미지 파일 선택 시 호출됨
  function handleFiles(files) {
    for (const file of files) {
      selectedFiles.push(file);
    }
    updatePreview();
  }

  // 미리보기 업데이트
  function updatePreview() {
    const container = document.getElementById('preview-container');
    container.innerHTML = '';

    selectedFiles.forEach((file, index) => {
      const reader = new FileReader();
      reader.onload = function (e) {
        const div = document.createElement('div');
        div.style.position = 'relative';
        div.style.display = 'inline-block';
        div.style.marginRight = '10px';

        const img = document.createElement('img');
        img.src = e.target.result;
        img.style.width = '100px';
        img.style.height = '100px';

        const delBtn = document.createElement('button');
        delBtn.innerText = '✕';
        delBtn.style.position = 'absolute';
        delBtn.style.top = '0';
        delBtn.style.right = '0';
        delBtn.style.background = 'red';
        delBtn.style.color = 'white';
        delBtn.style.border = 'none';
        delBtn.style.cursor = 'pointer';
        delBtn.style.fontSize = '12px';

        delBtn.onclick = () => {
          selectedFiles.splice(index, 1);
          updatePreview();
        };

        div.appendChild(img);
        div.appendChild(delBtn);
        container.appendChild(div);
      };
      reader.readAsDataURL(file);
    });
  }

  // 폼 제출 처리
  function submitForm(event) {
    event.preventDefault();

    const content = document.getElementById('tipContent').value;

    const formData = new FormData();
    formData.append('title', document.getElementById('title').value);
    formData.append('content', content);
    formData.append('startDate', document.getElementById('startDate').value);
    formData.append('finishDate', document.getElementById('finishDate').value);

    const imageInput = document.getElementById('images');
    for (const file of imageInput.files) {
      formData.append('images', file);
    }

    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch('/community/TipWrite', {
      method: 'POST',
      headers: { [header]: token },
      body: formData
    }).then(response => {
      if (response.ok) {
        alert('게시글이 등록되었습니다.');
        window.location.href = '/communityTip';
      } else {
        alert('작성에 실패했습니다.');
      }
    }).catch(() => {
      alert('서버 오류가 발생했습니다.');
    });
  }

  // 이벤트 연결
  document.getElementById('writeForm').addEventListener('submit', submitForm);
  window.handleFiles = handleFiles; // onchange에서 접근 가능하게 등록
});
