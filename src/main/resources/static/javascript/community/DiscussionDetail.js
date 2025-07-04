/* 댓글 */
function updateComment(commentId) {
  const contentEl = document.getElementById(`comment-content-${commentId}`);
  const formEl = document.getElementById(`update-form-${commentId}`);

  if (contentEl && formEl) {
    contentEl.style.display = 'none';
    formEl.style.display = 'block';
  }
}

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