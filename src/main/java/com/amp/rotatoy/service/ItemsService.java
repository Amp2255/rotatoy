package com.amp.rotatoy.service;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.amp.rotatoy.dto.ItemsDto;
import com.amp.rotatoy.mapper.ItemsMapper;
import com.amp.rotatoy.mapper.RotateActions;
import com.amp.rotatoy.model.Items;
import com.amp.rotatoy.repository.ItemsRepo;

@Service
public class ItemsService {
    private static Logger logger = LoggerFactory.getLogger(ItemsService.class);

       
    private ItemsMapper itemsMapper;
    private final ItemsRepo itemsRepo;
    public ItemsService(){
        this.itemsRepo = null;
        
    }
     private RotateActions rotateActions;
    @Autowired
    public ItemsService(ItemsRepo itemsRepo, ItemsMapper itemsMapper,RotateActions rotateActions){
        this.itemsRepo= itemsRepo;
        this.itemsMapper=itemsMapper;
        this.rotateActions=rotateActions;
    }

   public Page<Items> viewAllItems(String searchField,Pageable pageable){
    if(searchField.isEmpty()){
        return itemsRepo.findAll(pageable);
    }
    else{
        logger.info("******************* {}",searchField);
        return itemsRepo.searchField(searchField,pageable);
    }
   }
    
   public Items saveNewItem(Items items){
    logger.info("Item to save (name) is :{} ", items.getName());
    Items item = itemsRepo.save(items);
    logger.info("Save() return is :{} ", item.getName());
    return item;
   }

   public Items updateAnItem(String id,ItemsDto itemsDto){
    Items existingItem=itemsRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found: " + id)); 
    itemsMapper.updateItemFromDto(itemsDto, existingItem );
    return itemsRepo.save(existingItem);
   }

   public void deleteItemById(String id){
    try{
        itemsRepo.deleteById(id);   
    }
    catch (Exception e){
        logger.info("Exception occured while deleting : {}",e.getMessage());
    }
    
   }

   public Optional<Items> findById(String id){
    Optional<Items> optionalItem = Optional.ofNullable(new Items());
    try{
        optionalItem= itemsRepo.findById(id);   
        
    }
    catch (Exception e){
        logger.info("Exception occured while finding : {}",e.getMessage());
    }
    return optionalItem;
   }
    
   public Page<Items> findByFilter(String filter, Pageable pageable){  
    return itemsRepo.findByStatus(filter,pageable);  
   }

   public void updateAllItemsOnRotation(){
    //get list of stored items and itemsin rotation and update the status and date
    //list of stored items
    String status = "Stored";
    Optional<List<Items>> storedItemsList =itemsRepo.findListofItemsByStatus(status);
    status = "In Rotation";
    Optional<List<Items>> inrotationItemsList =itemsRepo.findListofItemsByStatus(status);
    if(storedItemsList.isPresent()){
        for(Items i : storedItemsList.get()){
            i.setStatus(rotateActions.updateItemStatus(i.getStatus()));
            i.setLastRotated(rotateActions.updateLastRotatedDate());
        }
       itemsRepo.saveAll(storedItemsList.get());
    }
    if(inrotationItemsList.isPresent()){
        for(Items i : inrotationItemsList.get()){
            i.setStatus(rotateActions.updateItemStatus(i.getStatus()));
            i.setLastRotated(rotateActions.updateLastRotatedDate());
        }
        itemsRepo.saveAll(inrotationItemsList.get());
    }

   }

   public void storeAllItems(){
    //get list of itemsin rotation and update the status and date
    String status = "In Rotation";
    
    Optional<List<Items>> inrotationItemsList =itemsRepo.findListofItemsByStatus(status);
    if(inrotationItemsList.isPresent()){
        for(Items i : inrotationItemsList.get()){
            i.setStatus(rotateActions.updateItemStatus(i.getStatus()));
            i.setLastRotated(rotateActions.updateLastRotatedDate());
        }
        itemsRepo.saveAll(inrotationItemsList.get());
    }

   }

}
