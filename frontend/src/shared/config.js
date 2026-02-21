export const config = {
    userAuthApiUrl: "http://localhost:8081/api/v1",
    coreBusinessApiUrl: "http://localhost:8082/api/v1",
    sms: {
        sendCodePath: "/sms/send-verification-code",
        verifyCodePath: "/sms/verify-phone",
        configPath: "/sms/config",
    },
};
