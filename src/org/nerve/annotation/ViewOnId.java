package org.nerve.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :View.java
 * @所在包 :org.nerve.annotation
 * @功能描述 :
 *	自动加载View的注解
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：广西可诺科技有限公司(http://www.chonour.com/)
 * @创建日期 :2013-6-6
 * @修改记录 :
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewOnId {
	
	/**
	 * view在layout中的id
	 *	@return
	 */
	public int id() default -1;
	
	/**
	 * 对于可以触发点击事件的 VIew，这个可以绑定点击事件监听器
	 * 默认为this，即为view的宿主类(一般是activity)
	 *	@return
	 */
	public String clickListener() default "";
}