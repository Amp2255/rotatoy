package com.amp.rotatoy.controller;

import org.springframework.web.bind.annotation.RestController;

import com.amp.rotatoy.dto.ApiResponse;
import com.amp.rotatoy.dto.ItemsDto;
import com.amp.rotatoy.mapper.ItemsMapper;
import com.amp.rotatoy.mapper.RotateActions;
import com.amp.rotatoy.model.Items;
import com.amp.rotatoy.service.ItemsService;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;

@RestController
public class HomeController {

    private static Logger logger = LoggerFactory.getLogger(HomeController.class);


    private final ItemsService itemsService;

     
    private ItemsMapper itemsMapper;

    private RotateActions rotateActions;
   
    public HomeController(ItemsService itemsService, ItemsMapper itemsMapper, RotateActions rotateActions){
        this.itemsService = itemsService;
        this.itemsMapper = itemsMapper;
        this.rotateActions =rotateActions;
    }
    
    @GetMapping("/item")
    public ResponseEntity<ApiResponse<Page<Items>>> viewAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String searchField
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        try{
            if(status.isEmpty() || status.isBlank()){
                logger.info("searcgfield is :{}",searchField);
            Page <Items> itemsPage = itemsService.viewAllItems(searchField,pageable);
            if (itemsPage.getTotalElements()==0){   
                ApiResponse<Page<Items>> response = new ApiResponse<>(true, "No items found", itemsPage);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
            else{
                ApiResponse<Page<Items>> response = new ApiResponse<>(true, "Fetched all items", itemsPage);
                return new ResponseEntity<>(response, HttpStatus.OK);
            
            }
            }
            else{
                Page <Items> opsItem = itemsService.findByFilter(status,pageable);
                if(!opsItem.hasContent()){
                    ApiResponse<Page <Items>>response = new ApiResponse<>(false,"No item found",null);
                    return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
                }else{
                    ApiResponse<Page <Items>>response = new ApiResponse<>(true,"Item found",opsItem);
                    return new ResponseEntity<>(response,HttpStatus.OK);
                }
            }
            
        }
        catch(Exception e){
            ApiResponse<Page<Items>> response = new ApiResponse<>(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            
        }
       
    }
    
    @PostMapping("item")
    public ResponseEntity<ApiResponse<Items>> saveNewItem(@RequestBody ItemsDto itemsDto) {
        try{
            logger.info("Item to save (name) is :{} ", itemsDto.getName());
            Items itemToSave = itemsMapper.toEntity(itemsDto);
            Items saved = itemsService.saveNewItem(itemToSave);
            logger.info("Item to saved is :{} ", saved.getName());
            if (saved.getId() == null){
                ApiResponse<Items> response = new ApiResponse<>(false, "Item could not be saved", saved);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else{
                ApiResponse<Items> response = new ApiResponse<>(true, "New item saved", saved);
                return new ResponseEntity<>(response, HttpStatus.OK);
            
            }
        }
        catch(Exception e){
            ApiResponse<Items> response = new ApiResponse<>(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @PatchMapping("item")
    public ResponseEntity<ApiResponse<Items>> updateAnItem(@RequestParam String id,@RequestBody ItemsDto itemsDto) {
       try{
        Items updatedItem = itemsService.updateAnItem(id, itemsDto);
        if(updatedItem.getName().isEmpty()){
            ApiResponse<Items>response = new ApiResponse<>(false,"Update failed",null);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            ApiResponse<Items>response = new ApiResponse<>(true,"Item updated",updatedItem);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
       }
       catch(Exception e){
            ApiResponse<Items> response = new ApiResponse<>(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("item")
    public ResponseEntity<ApiResponse<String>> deleteItemById(@RequestParam String id){
        try{
            if(id.isEmpty()){
                ApiResponse<String> response = new ApiResponse<>(false,"Id empty", null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }else{
                itemsService.deleteItemById(id);
                ApiResponse<String> response = new ApiResponse<>(true,"Item deleted successfully", "Id deleted:"+id);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        catch(Exception e){
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        
        }
    }

    @GetMapping("item/id/")
    public ResponseEntity<ApiResponse<Items>> getItemById(@RequestParam String id) {
        try{
        Optional<Items> opsItem = itemsService.findById(id);
        if(!opsItem.isPresent()){
            ApiResponse<Items>response = new ApiResponse<>(false,"No item found",null);
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }else{
            ApiResponse<Items>response = new ApiResponse<>(true,"Item found",opsItem.get());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
       }
       catch(Exception e){
            ApiResponse<Items> response = new ApiResponse<>(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PatchMapping("item/rotate")
    public ResponseEntity<ApiResponse<Items>> rotateAnItem(@RequestParam String id,@RequestBody ItemsDto itemsDto) {
       try{
        itemsDto.setStatus(rotateActions.updateItemStatus(itemsDto.getStatus()));
        itemsDto.setLastRotated(rotateActions.updateLastRotatedDate());
        Items updatedItem = itemsService.updateAnItem(id, itemsDto);
        if(updatedItem.getName().isEmpty()){
            ApiResponse<Items>response = new ApiResponse<>(false,"Update failed",null);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            ApiResponse<Items>response = new ApiResponse<>(true,"Item updated",updatedItem);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
       }
       catch(Exception e){
            ApiResponse<Items> response = new ApiResponse<>(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("item/rotateAll")
    public ResponseEntity<ApiResponse<String>> rotateAllItems 
    () {
        try{
            logger.info("in controller rotate all");
            itemsService.updateAllItemsOnRotation();
            ApiResponse<String>response = new ApiResponse<>(true,"Updated successfully","");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch(Exception e){
            ApiResponse <String>response = new ApiResponse<>(false,"Update failed",null);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        

    } 
    @PatchMapping("item/storeAll")
    public ResponseEntity<ApiResponse<String>> storeAllItems 
    () {
        try{
            logger.info("in controller rotate all");
            itemsService.storeAllItems();
            ApiResponse<String>response = new ApiResponse<>(true,"Updated successfully","");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch(Exception e){
            ApiResponse <String>response = new ApiResponse<>(false,"Update failed",null);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        

    } 
}
