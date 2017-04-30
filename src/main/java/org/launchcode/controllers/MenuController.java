package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by jeannie on 4/29/17.
 */
@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute("menu", new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(@ModelAttribute @Valid Menu menu, Errors errors,
                      @RequestParam int id, Model model) {
        if(errors.hasErrors()){
            model.addAttribute("title", "Add Menu");
            return"menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    // What is the syntax for including variable in the RequestMapping value?
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/", method = RequestMethod.GET)
    public String addItem(@PathVariable int id, Model model) {
        Menu menu = menuDao.findOne(id);
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, menu.getCheeses());
        model.addAttribute("form", addMenuItemForm);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm addMenuItemForm,
                          int menuId, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("title", "Add item to menu: " + menuDao.findOne(menuId));
            model.addAttribute("form", addMenuItemForm);
            return "menu/add-item";
        }

        Cheese cheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        Menu menu = menuDao.findOne(addMenuItemForm.getMenuId());
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }




}
