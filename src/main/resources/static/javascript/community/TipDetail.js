function updateComment(commentId) {
  const contentEl = document.getElementById(`comment-content-${commentId}`);
  const formEl = document.getElementById(`update-form-${commentId}`);

  if (contentEl && formEl) {
    contentEl.style.display = 'none';
    formEl.style.display = 'block';
  }
}
