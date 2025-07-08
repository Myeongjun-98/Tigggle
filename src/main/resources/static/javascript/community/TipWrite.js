document.addEventListener("DOMContentLoaded", () => {
  let selectedFiles = [];

  // 기본 이미지 미리보기 처리
  const existingImages = document.querySelectorAll('.existing-image');
  existingImages.forEach((div) => {
    const url = div.dataset.url;
    const id = div.dataset.id;

  const container = document.getElementById('preview-container');

  const wrapper = document.createElement('div');
  wrapper.style.position = 'relative';
  wrapper.style.display = 'inline-block';
  wrapper.style.marginRight = '10px';

  const img = document.createElement('img');
  img.src = url;
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
    wrapper.remove();

    const hiddenInput = document.createElement('input');
    hiddenInput.type = 'hidden';
    hiddenInput.name = 'deleteImageIds';
    hiddenInput.value = id;
    document.getElementById('writeForm').appendChild(hiddenInput);
  };

  wrapper.appendChild(img);
  wrapper.appendChild(delBtn);

  container.appendChild(wrapper);
});

  // 이미지 파일 선택 시 호출됨
  window.handleFiles = function (files) {
    for(const file of files) {
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

      const formData = new FormData();
      formData.append('title', document.getElementById('title').value);
      formData.append('content', document.getElementById('tipContent').value);

      // 새로 추가한 이미지들
      for (const file of selectedFiles) {
        formData.append('images', file);
      }

      // 삭제된 이미지 ID들 (이미 hidden input으로 폼에 추가됨)
      document.querySelectorAll('input[name="deleteImageIds"]').forEach(input => {
        formData.append('deleteImageIds', input.value);
      });

      const token = document.querySelector('meta[name="_csrf"]').content;
      const header = document.querySelector('meta[name="_csrf_header"]').content;

      const isEdit = window.location.pathname.includes('/edit/');
      const url = isEdit ? window.location.pathname : '/community/TipWrite';

      fetch(url, {
        method: 'POST',
        headers: { [header]: token },
        body: formData
      }).then(response => {
        if (response.ok) {
          alert(isEdit ? '게시글이 수정되었습니다.' : '게시글이 등록되었습니다.');
          window.location.href = '/communityTip';
        } else {
          alert(isEdit ? '수정에 실패했습니다.' : '작성에 실패했습니다.');
        }
      }).catch(() => {
        alert('서버 오류가 발생했습니다.');
      });
    }

    document.getElementById('writeForm').addEventListener('submit', submitForm);
  });


