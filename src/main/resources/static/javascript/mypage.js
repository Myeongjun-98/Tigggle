$("#profile").change(function(){
    const file = this.files[0];

    // 1. 미리보기 처리
    if (file && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = function(e) {
            $('#profilePic').css('background-image', `url(${e.target.result})`);
        };
        reader.readAsDataURL(file);
    }

   // 2. 서버로 전송
    const formData = new FormData();
    formData.append("profile", file);
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');
    $.ajax({
        url: '/user/profile/upload',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(data) {
            if (!data.success) {
                alert("이미지 업로드 실패: " + data.message);
            }else{
                alert("성공");
            }

        },
        error: function(err) {
            console.error("업로드 중 오류:", err);
            alert("서버 오류로 업로드 실패");
        }
    });
});