package hc.gras.annotations;





import java.lang.annotation.*;

/**
 * <p>
 *
 * </p>
 *
 * @author hao.wong
 * @since 2023/1/4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GrpcServiceClient  {

    /**
     * 服务地址
     * @return
     */
    String address();


    Class<?> serviceGrpc();
}
