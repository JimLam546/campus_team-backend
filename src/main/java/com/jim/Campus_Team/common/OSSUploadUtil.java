package com.jim.Campus_Team.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者:
 * 日期: 2024-05-29 22:59
 */

public class OSSUploadUtil {

    public static String upload(MultipartFile file, long userId, String type) {
        String fileName = null;
        try {
            InputStream is = file.getInputStream();
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            String endpoint = "oss-cn-shenzhen.aliyuncs.com";
            // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，
            // 请登录 https://ram.console.aliyun.com 创建RAM账号。
            String accessKeyId = "LTAI5t8fbTFmiPmB1QNf3u1G";
            String accessKeySecret = "GwW47S1E8s21K1GJuVhOszW7qvHfIW";

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);


            String bucketName = "jim-one-project";


            fileName =  OSSUploadUtil.avatar(userId, file, type);
            //bucketName是你的ossBucket的名称，fileName是需要存储文件的名称
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, is);

            fileName = "https://" + bucketName + "." + endpoint + "/" + fileName;
            // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // 设置上传的文件权限
            metadata.setObjectAcl(CannedAccessControlList.PublicRead);
            putObjectRequest.setMetadata(metadata);

            ossClient.putObject(putObjectRequest);

            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private static String avatar(long userId, MultipartFile file, String type) {
        String id = String.valueOf(userId);
        String fileName =  id + "--" + "--" + file.getOriginalFilename();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String yearMonth = format.format(date);
        return "avatar/" + type + "/" + yearMonth + "/" + fileName;
    }
}