/* 이미지 클릭시 모달창으로 확대 */
function openModal(src) {
  const modal = document.getElementById("imageModal");
  const modalImg = document.getElementById("modalImage");

  modal.style.display = "block";
  modalImg.src = src;
}

function closeModal() {
  document.getElementById("imageModal").style.display = "none";
}

function enableEdit() {
  document.getElementById("view-title").style.display = "none";
  document.getElementById("edit-title").style.display = "block";

  document.getElementById("view-content").style.display = "none";
  document.getElementById("edit-content").style.display = "block";

  document.getElementById("edit-buttons").style.display = "block";
  document.getElementById("new-image-upload").style.display = "block";

  // 이미지 삭제 버튼 활성화
  document.querySelectorAll(".image-container input[type='checkbox']").forEach(cb => {
    cb.style.display = "inline";
  });
}

function markImageForDeletion(button) {
  const container = button.closest('.image-container');
  const checkbox = container.querySelector('input[type="checkbox"]');
  if (checkbox) {
    checkbox.checked = !checkbox.checked;
    container.style.opacity = checkbox.checked ? "0.5" : "1";
    button.innerText = checkbox.checked ? "복원" : "삭제";
  }
}

/* 댓글 */
function updateComment(commentId) {
  // 댓글 본문 숨김, 수정 폼 표시
  document.getElementById(`comment-content-${commentId}`).style.display = "none";
  document.getElementById(`update-form-${commentId}`).style.display = "block";

  // 수정 / 삭제 버튼 숨김
  const buttonArea = document.getElementById(`action-buttons-${commentId}`);
  if (buttonArea) {
    buttonArea.style.display = "none";
  }
}

function cancelUpdate(commentId) {
  // 댓글 본문 표시, 수정 폼 숨김
  document.getElementById(`comment-content-${commentId}`).style.display = "block";
  document.getElementById(`update-form-${commentId}`).style.display = "none";

  // 수정 / 삭제 버튼 다시 표시
  const buttonArea = document.getElementById(`action-buttons-${commentId}`);
  if (buttonArea) {
    buttonArea.style.display = "block";
  }
}



