package com.festivo.domain.services.interfaces;

import com.amazonaws.SdkClientException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadDownloadService {

    String uploadFile(MultipartFile file) throws SdkClientException, IOException;

    Resource downloadFile(String fileName) throws IOException;
}
