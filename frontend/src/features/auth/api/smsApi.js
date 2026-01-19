import { http } from "../../../shared/http/http";
import { config } from "../../../shared/config";

export const smsApi = {
    getSmsConfig() {
        return http(config.sms.configPath, {
            method: "GET",
            withAuth: false,
        });
    },

    sendCode(phone) {
        return http(config.sms.sendCodePath, {
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
