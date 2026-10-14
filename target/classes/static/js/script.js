// JavaScript helper scripts for Mentor-Mentee Management System

document.addEventListener('DOMContentLoaded', function () {
    // Password confirm check hint
    const pwdInput = document.getElementById('password');
    const confirmPwdInput = document.getElementById('confirmPassword');
    const matchHint = document.getElementById('passwordMatchHint');

    if (pwdInput && confirmPwdInput && matchHint) {
        function checkMatch() {
            if (confirmPwdInput.value === '') {
                matchHint.textContent = '';
                return;
            }
            if (pwdInput.value === confirmPwdInput.value) {
                matchHint.textContent = '✓ Passwords match';
                matchHint.className = 'form-text text-success font-weight-bold';
            } else {
                matchHint.textContent = '✗ Passwords do not match';
                matchHint.className = 'form-text text-danger font-weight-bold';
            }
        }

        pwdInput.addEventListener('input', checkMatch);
        confirmPwdInput.addEventListener('input', checkMatch);
    }
});
