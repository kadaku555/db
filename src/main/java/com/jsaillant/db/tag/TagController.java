package com.jsaillant.db.tag;

import com.jsaillant.db.exception.EntityAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TagController {

    @Autowired
    private TagService tagService;

    @RequestMapping(value = "/tags", produces = "Application/json")
    public List<TagDTO> listTags() {
        return tagService.list().stream().map(t -> new TagDTO(t)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public ResponseEntity createTag(@RequestParam("name") String name) {
        ResponseEntity response;
        try {
            tagService.create(name);
            response = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
