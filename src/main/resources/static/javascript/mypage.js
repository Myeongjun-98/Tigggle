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

function myInfoUpdate(){
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');

    const genderValue = $('select[name="gender"]').val();


    const formData = {
        tel: $('input[name="tel"]').val(),
        email: $('input[name="email"]').val(),
        gender: genderValue === '1',
        birthday: $('input[name="birthday"]').val()
    };

    $.ajax({
        url: '/user/profile/update',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(response) {
            alert("정보가 성공적으로 수정되었습니다.");
            // 필요시 페이지 새로고침 or 반영
            // location.reload();
        },
        error: function(error) {
            alert("정보 수정에 실패했습니다.");
            console.error(error);
        }
    });
}