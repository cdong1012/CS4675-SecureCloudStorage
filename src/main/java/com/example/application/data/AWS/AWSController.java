package com.example.application.data.AWS;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Versions;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

// NOTE: https://www.baeldung.com/aws-s3-java

public class AWSController {
    private static AWSCredentials credentials;
    private final static String accessKey = "<redacted>";
    private final static String secretKey = "<redacted>";
    private static AmazonS3 s3client;

    /**
     * Connect to AWS S3 server. Call this first in main.
     */
    public static void createClientConnection() {
        credentials = new BasicAWSCredentials(AWSController.accessKey, AWSController.secretKey);
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    /**
     * Create a bucket
     * @param bucketName Bucket name
     */
    public static void createBucket(String bucketName) {
        if (s3client.doesBucketExistV2(bucketName)) {
            System.out.println("[x] Bucket " + bucketName + " exists... Please try again with a different name!");
            return;
        }

        s3client.createBucket(bucketName);
    }

    public static void listBuckets() {
        List<Bucket> buckets = s3client.listBuckets();
        for(Bucket bucket : buckets) {
            System.out.println(bucket.getName());
        }
    }

    /**
     * List objects (files in bucket)
     * NOTE: An S3ObjectSummary object stores file size, file owner (no need database for this), ...
     * @param bucketName Name of bucket
     * @return List of
     */
    public static List<S3ObjectSummary> listObjects(String bucketName) {
        if (s3client.doesBucketExistV2(bucketName)) {
            ObjectListing objectListing = s3client.listObjects(bucketName);
            return objectListing.getObjectSummaries();
        }
        System.out.println("[x] Bucket does not exist... Please try again");
        return null;
    }

    public static List<String> listObjectNames(String bucketName) {
        List<S3ObjectSummary> listObjects = listObjects(bucketName);
        List<String> result = new ArrayList<>();

        for (S3ObjectSummary each : listObjects) {
            result.add(each.getKey());
        }

        return result;

    }


    /**
     * Download a file from a bucket
     * @param bucketName Name of Bucket
     * @param objectName Name of file
     * @param outPath Output path to write file to.
     */
    public static void downloadObject(String bucketName, String objectName, String outPath) {
        if (s3client.doesBucketExistV2(bucketName)) {
            S3Object s3object = s3client.getObject(bucketName, objectName);
            S3ObjectInputStream inputStream = s3object.getObjectContent(); // file stream
            File outFile = new File(outPath);
            try {
                FileOutputStream outputStream = new FileOutputStream(outFile, false);
                int read;
                byte[] bytes = new byte[8192];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                outputStream.close();
            } catch (Exception exception) {
                System.err.println(exception);
            }
            System.out.println("Download file " + objectName + " successfully.");
        } else {
            System.out.println("[x] Bucket does not exist... Please try again");
        }
    }

    public static void uploadObject(String bucketName, String filePath, String fileName) {
        File file = new File(filePath);
        if (file.exists()) {
            s3client.putObject(bucketName, fileName, file);
            System.out.println("Upload file successfully");
        }
    }

    public static void cleanBuckets() {
        List<Bucket> buckets = s3client.listBuckets();
        List<String> bucketNames = new ArrayList<>();

        for (Bucket bucket : buckets) {
            bucketNames.add(bucket.getName());
        }

        for (String bucketName : bucketNames) {
            List<S3ObjectSummary> listObjects = listObjects(bucketName);

            for (S3ObjectSummary each : listObjects) {
                s3client.deleteObject(bucketName, each.getKey());
            }

            for ( S3VersionSummary version : S3Versions.inBucket(s3client, bucketName) ) {
                String key = version.getKey();
                String versionId = version.getVersionId();
                s3client.deleteVersion(bucketName, key, versionId);
            }

            s3client.deleteBucket(bucketName);
        }
    }

    public static void deleteFile(String bucketName, String fileName) {
        s3client.deleteObject(bucketName, fileName);
    }

    public static void cleanBucket(String bucketName) {
        List<S3ObjectSummary> listObjects = listObjects(bucketName);

        for (S3ObjectSummary each : listObjects) {
            s3client.deleteObject(bucketName, each.getKey());
        }

        for ( S3VersionSummary version : S3Versions.inBucket(s3client, bucketName) ) {
            String key = version.getKey();
            String versionId = version.getVersionId();
            s3client.deleteVersion(bucketName, key, versionId);
        }

        s3client.deleteBucket(bucketName);
    }

}
