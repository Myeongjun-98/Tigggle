document.addEventListener("DOMContentLoaded", () => {
  let selectedFiles = [];
  const existingImageData = []; // 기존 이미지 저장
  const container = document.getElementById('preview-container');

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

    // 삭제된 기존 이미지를 리스트에서도 제거
      const index = existingImageData.findIndex(img => img.id === id);
      if (index !== -1) {
        existingImageData.splice(index, 1);
    }
  };
  wrapper.appendChild(img);
  wrapper.appendChild(delBtn);

  container.appendChild(wrapper);

  existingImageData.push({id, url}); // 보존용
});

  // 이미지 파일 선택 시 호출됨
  window.handleFiles = function (files) {
      const totalExisting = selectedFiles.length + existingImageData.length;

        // 5장을 초과하는 경우에는 아무것도 추가하지 않고 경고만 띄움
        if (totalExisting + files.length > 5) {
            alert('사진은 5장까지 첨부 가능합니다.');
            return;
        }

        // 중복 아닌 파일만 추가
          for (const file of files) {
            const isDuplicate = selectedFiles.find(
              f => f.name === file.name && f.lastModified === file.lastModified
            );
            if (!isDuplicate) {
              selectedFiles.push(file);
            }
          }

          updatePreview();
        };

  // 미리보기 업데이트
    // 새로 추가된 이미지 미리보기만 렌더링
      function updatePreview() {
        // 모든 새 이미지 미리보기 제거 후 다시 렌더링
        // 기존 이미지(wrapper)는 유지, 새 이미지만 다시 렌더링
        // 기존 이미지 개수 = existingImageData.length
        const wrappers = container.querySelectorAll('div');

        // 새 이미지 미리보기만 삭제
        wrappers.forEach((wrapper, index) => {
          if (index >= existingImageData.length) {
            container.removeChild(wrapper);
          }
        });

        // 새 이미지 다시 렌더링
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
              updatePreview(); // 재갱신
            };

            div.appendChild(img);
            div.appendChild(delBtn);
            container.appendChild(div);
          };
          reader.readAsDataURL(file);
        });
      }

  let isSubmitting = false; // 중복 submit 방지

  // 폼 제출 처리
    function submitForm(event) {
      event.preventDefault();

      if (isSubmitting) return; // 이미 제출 중이면 무시
        isSubmitting = true;

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
      const url = isEdit ? window.location.pathname : '/Tigggle/community/TipWrite';

      fetch(url, {
        method: 'POST',
        headers: { [header]: token },
        body: formData
      }).then(response => {
        if (response.ok) {
          alert(isEdit ? '게시글이 수정되었습니다.' : '게시글이 등록되었습니다.');
          window.location.href = '/Tigggle/communityTip';
        } else {
          alert(isEdit ? '수정에 실패했습니다.' : '작성에 실패했습니다.');
          isSubmitting = false;
        }
      }).catch(() => {
        alert('서버 오류가 발생했습니다.');
        isSubmitting = false;
      });
    }

    document.getElementById('writeForm').addEventListener('submit', submitForm);
  });


