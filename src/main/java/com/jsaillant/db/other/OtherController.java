package com.jsaillant.db.other;

import com.jsaillant.db.exception.EntityNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OtherController {

    private static final String BASE_PATH = "F:/portable/samsung/video/";
    private static final String INPUT_PATH = "";

    @RequestMapping("/h")
    public List list(@RequestParam(value = "folder", defaultValue = INPUT_PATH) String folder) {
        List<Map<String, Object>> res = new ArrayList<>();
        File origin = new File(BASE_PATH + folder);
        if (!origin.isDirectory()) {
            throw new IllegalArgumentException("Le chemin doit correspondre à un répertoire.");
        }
        for (File h : origin.listFiles()) {
            if (h.isFile()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", h.getName());
                map.put("path", h.getAbsolutePath().replace("\\", "/"));
                map.put("date", h.lastModified());
                res.add(map);
            }
        }
        return res;
    }

    @RequestMapping(value = "/h/video", produces = "video/mp4")
    public FileSystemResource getVideo(@RequestParam("path") String path) {
        return new FileSystemResource(new File(path));
    }

    @RequestMapping(value = "/h/moveto/{dest}/as/{as}")
    public ResponseEntity move(@RequestParam("path") String filePath, @PathVariable("dest") String destPath, @PathVariable("as") String as) {
        ResponseEntity response;
        File input = new File(filePath);
        File destFolder = new File(BASE_PATH + destPath);
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }
        File dest = new File(destFolder, destPath + " - " + as + input.getName().substring(input.getName().lastIndexOf(".")));
        if (input.renameTo(dest)) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response = new ResponseEntity<>("File can't be moved!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
