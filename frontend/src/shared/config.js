export const config = {
    apiBaseUrl: "http://localhost:8080/api/v1",
    sms: {
        sendCodePath: "/sms/send-verification-code",
        resendCodePath: "/sms/resend-code",
        verifyCodePath: "/sms/verify-phone",
    },
};
