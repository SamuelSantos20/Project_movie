package com.br.sb.project_movie.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileConverter {

    public static MultipartFile toMultipartFile(byte[] bytes) {
        return new MockMultipartFile("file", "filename.txt", null, bytes);
    }
}
