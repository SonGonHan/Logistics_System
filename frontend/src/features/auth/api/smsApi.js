import { http } from "../../../shared/http/http";
import { config } from "../../../shared/config";

export const smsApi = {
    sendCode(phone) {
        return http(config.sms.sendCodePath, {
            method: "POST",
            body: { phone },
            withAuth: false,
        });
    },

    resendCode(phone) {
        return http(config.sms.resendCodePath, {
            method: "POST",
            body: { phone },
            withAuth: false,
        });
    },

    verifyCode(phone, code) {
        return http(config.sms.verifyCodePath, {
            method: "POST",
            body: { phone, code },
            withAuth: false,
        });
    },
};
