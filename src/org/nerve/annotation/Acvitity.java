package org.nerve.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :Acvitity.java
 * @所在包 :org.nerve.annotation
 * @功能描述 :
 *	整个activity的注解
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：广西可诺科技有限公司(http://www.chonour.com/)
 * @创建日期 :2013-6-6
 * @修改记录 :
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Acvitity {
	/**
	 * 对应的layout文件
	 *	@return
	 */
	public int layout() default 0;
	
}