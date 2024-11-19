Authentication using JWT along with 
  1. the OTP service,
  2. the national code and phone number matching service.

APIs:
  1. **signup/login**: 127.0.0.1:8080/api/user/register
       signup/login with nationalId and phone number.
       OTP token sends to phone number is disposable and valid for just 2 minutes.
  3. **OTP**: 127.0.0.1:8080/api/user/otp:
       Send phoneNumber and OTP token and **get JWT** token.
  3. **USER**: : 127.0.0.1:8080/api/user/me:
       Send JWT token starting with "Bearer " in "Authorization" header's field to get user info. 
     
