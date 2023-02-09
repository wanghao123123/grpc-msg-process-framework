package com.example.grpcserviceclientmsgframe.javaassist;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *
 * </p>
 *
 * @author hao.wong
 * @since 2022/12/14
 */
@RestController
@RequestMapping("/client")
public class JavaAssistTest {




    @Getter
    @Setter
    static class T {
        private Long c;
    }

    public static String xx(){
        int length = 10;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(62);
            char nextChar;
            if (randomIndex < 26) {
                nextChar = (char)('A' + randomIndex);
            } else if (randomIndex < 52) {
                nextChar = (char)('a' + (randomIndex - 26));
            } else {
                nextChar = (char)('0' + (randomIndex - 52));
            }
            sb.append(nextChar);
        }
        String result = sb.toString();
        return result;
    }

    public static void main(String[] args) throws IOException, NotFoundException, CannotCompileException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        methodTest();
        CitiesVo citiesVo = new CitiesVo();
        citiesVo.setName("dahkjdhajkdhak");

        CitiesVo citiesVo2 = new CitiesVo();
        citiesVo2.setName("dahkjdhajkdhak");


    }

    public static void methodTest() throws NotFoundException, CannotCompileException, IOException {
        //默认的类搜索路径
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.example.testclient.controller.CitiesVo");
        CtMethod[] setNames = ctClass.getDeclaredMethods("setName");

        for (CtMethod setName : setNames) {
            setName.insertBefore("{System.out.println($1);}");

            setName.insertAfter("{$0.getName();}");

        }

        ctClass.toClass();
        ctClass.detach();

    }

    public static void setMethodBody() throws IOException, CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //默认的类搜索路径
        ClassPool pool = ClassPool.getDefault();
//        //获取一个ctClass对象 com.example.demo.excel.entity.CitiesVo 这个是包的相对路径
//        CtClass ctClass = pool.get("com.example.testclient.controller.CitiesVo");

        String xx = xx();
        CtClass ctClass = pool.makeClass(xx);
        List<String> fields = Stream.of("w", "ss", "da1", "dad").collect(Collectors.toList());
        for (String field : fields) {

            //创建字段，指定了字段类型、字段名称、字段所属的类
            CtField ctField = new CtField(pool.get("java.lang.String"), field, ctClass);
            //指定该字段使用private修饰
            ctField.setModifiers(Modifier.PRIVATE);
            //设置age字段的getter/setter方法
            ctClass.addMethod(CtNewMethod.setter("set" + field, ctField));
            ctClass.addMethod(CtNewMethod.getter("get" + field, ctField));
            //将age字段添加到clazz中
            ctClass.addField(ctField);

            //获取这个字段
            FieldInfo fieldInfo = ctField.getFieldInfo();
            ConstPool cp = fieldInfo.getConstPool();
            AnnotationsAttribute attribute = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
            //这里进行了判断 如果说当前字段没有注解的时候 AnnotationsAttribute 这个对象是为空的
            //所以要针对这个进行新创建 一个 AnnotationsAttribute 对象
            if(ObjectUtils.isEmpty(attribute)){
                List<AttributeInfo> attributeInfos =fieldInfo.getAttributes();
                attribute = !attributeInfos.isEmpty()?(AnnotationsAttribute) attributeInfos.get(0):
                        new AnnotationsAttribute(fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag);
            }
            // Annotation 默认构造方法  typeName:表示的是注解的路径
            Annotation bodyAnnot = new Annotation("cn.afterturn.easypoi.excel.annotation.Excel", cp);
            // name 表示的是自定义注解的 方法  new StringMemberValue("名字", cp) 表示给name赋值
            bodyAnnot.addMemberValue("name", new StringMemberValue(field, cp));
            attribute.addAnnotation(bodyAnnot);
            fieldInfo.addAttribute(attribute);

            System.err.println();

        }

        /**
         * 修改方法内容
         */

        ctClass.defrost();
        Arrays.stream(ctClass.getDeclaredMethods()).forEach(a-> {
            if(a.getName().equals("setw")){
                try {
                    a.setBody("{System.out.println($1);}");
                } catch (CannotCompileException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ctClass.writeFile();
        Object o = ctClass.toClass().newInstance();
        Method setName = o.getClass().getMethod("setw", String.class);
        setName.invoke(o,"Y4er");
    }

    /**
     * excel导出
     * @throws IOException
     * @throws CannotCompileException
     */
    public static void excel() throws IOException, CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException {
        //默认的类搜索路径
        ClassPool pool = ClassPool.getDefault();
//        //获取一个ctClass对象 com.example.demo.excel.entity.CitiesVo 这个是包的相对路径
//        CtClass ctClass = pool.get("com.example.testclient.controller.CitiesVo");

        String xx = xx();
        CtClass ctClass = pool.makeClass(xx);

        List<String> fields = Stream.of("w", "ss", "da1", "dad").collect(Collectors.toList());

        for (String field : fields) {

            //创建字段，指定了字段类型、字段名称、字段所属的类
            CtField ctField = new CtField(pool.get("java.lang.String"), field, ctClass);
            //指定该字段使用private修饰
            ctField.setModifiers(Modifier.PRIVATE);
            //设置age字段的getter/setter方法
            ctClass.addMethod(CtNewMethod.setter("set" + field, ctField));
            ctClass.addMethod(CtNewMethod.getter("get" + field, ctField));
            //将age字段添加到clazz中
            ctClass.addField(ctField);

            //获取这个字段
            FieldInfo fieldInfo = ctField.getFieldInfo();
            ConstPool cp = fieldInfo.getConstPool();
            AnnotationsAttribute attribute = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
            //这里进行了判断 如果说当前字段没有注解的时候 AnnotationsAttribute 这个对象是为空的
            //所以要针对这个进行新创建 一个 AnnotationsAttribute 对象
            if(ObjectUtils.isEmpty(attribute)){
                List<AttributeInfo> attributeInfos =fieldInfo.getAttributes();
                attribute = !attributeInfos.isEmpty()?(AnnotationsAttribute) attributeInfos.get(0):
                        new AnnotationsAttribute(fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag);
            }
            // Annotation 默认构造方法  typeName:表示的是注解的路径
            Annotation bodyAnnot = new Annotation("cn.afterturn.easypoi.excel.annotation.Excel", cp);
            // name 表示的是自定义注解的 方法  new StringMemberValue("名字", cp) 表示给name赋值
            bodyAnnot.addMemberValue("name", new StringMemberValue(field, cp));
            attribute.addAnnotation(bodyAnnot);
            fieldInfo.addAttribute(attribute);

            System.err.println();

        }

        //        todo 贼他妈重要这句话
        Class<?> aClass = ctClass.toClass();
        List<Object> list =new ArrayList<>();
//        模拟excel数据
        for (int i = 0; i < 10; i++) {
            Object o =  aClass.newInstance();

            ReflectionUtils.doWithFields(o.getClass(),a->{
                a.setAccessible(true);
                a.set(o,"abc"+ new Random().nextInt(10) +"---");
            });
            list.add(o);

        }

        ExportParams params = new ExportParams("课程详情", null, "课程详情");
        Workbook workbook = ExcelExportUtil.exportExcel(params, aClass, list);
        File targetFile = new File("temp.xls");
        FileOutputStream fos = new FileOutputStream(targetFile);
        workbook.write(fos);
        fos.close();
        ctClass.detach();
    }




}
