package io.nebula.market.product.domain.exception;

import dev.akkinoc.util.YamlResourceBundle;

import java.util.ResourceBundle;

public class BizException extends RuntimeException {
    private static final ResourceBundle bundle = ResourceBundle.getBundle(
            "messages/exception", YamlResourceBundle.Control.INSTANCE);

    private BizException(String key) {
        super(bundle != null ? bundle.getString(key) : "설정파일을 조회할 수 없습니다");
    }

    public static class NoneExists extends BizException {
        public NoneExists() {
            super("product.none_exists");
        }
    }

}
