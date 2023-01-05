package com.example.grpcserviceclientmsgframe.grpc;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrpcHeaders {
    private String lang;
    private String currency;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    enum LangEnums {
        en("en", "en_US"), zh("zh-CN", "zh_CN"), en_us("en-US", "en_US"), zh_cn("zh-CN", "zh_CN");

        @Getter
        private final String standard;
        @Getter
        private final String backEnd;
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    enum CurrencyEnums {
        USDT("USDT"), CNY("CNY");

        @Getter
        private final String coin;

    }

    public String getLang() {
        String r = LangEnums.zh.getBackEnd();
        for (LangEnums l : LangEnums.values()) {
            if (lang.equals(l.getStandard())) {
                r = l.getBackEnd();
            }
        }
        return r;
    }

    public String getCurrency() {
        String r = CurrencyEnums.USDT.getCoin();
        for (CurrencyEnums c : CurrencyEnums.values()) {
            if (currency.equals(c.getCoin())) {
                r = c.getCoin();
            }
        }

        return r;

    }
}
