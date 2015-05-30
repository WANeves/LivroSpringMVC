package br.com.casadocodigo.loja.controllers;

import java.io.IOException;

import javax.servlet.http.Part;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.casadocodigo.loja.daos.ProductDAO;
import br.com.casadocodigo.loja.infra.FileSaver;
import br.com.casadocodigo.loja.infra.HttpPartUtils;
import br.com.casadocodigo.loja.models.BookType;
import br.com.casadocodigo.loja.models.Product;

@Controller
@Transactional
@RequestMapping("/produtos")
public class ProductsController {

	@Autowired
	private ProductDAO products;
	@Autowired
	private FileSaver fileSaver;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// binder.setValidator(new ProductValidator());
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView save(Part summary,
			@ModelAttribute("product") @Valid Product product,
			BindingResult bindingResult, RedirectAttributes redirectAttributes)
			throws IOException {
		if (bindingResult.hasErrors()) {
			return form(product);
		}

		// Sera que passo o product como parametro?
		String webPath = fileSaver.write("uploaded-images",
				HttpPartUtils.extractFileName(summary), summary);
		product.setSummaryPath(webPath);
		products.save(product);

		redirectAttributes.addFlashAttribute("success",
				"Produto cadastrado com sucesso");
		return new ModelAndView("redirect:produtos");
	}

	@RequestMapping("/form")
	public ModelAndView form(@ModelAttribute Product product) {
		ModelAndView modelAndView = new ModelAndView("products/form");
		modelAndView.addObject("bookTypes", BookType.values());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView list(Model model) {
		ModelAndView modelAndView = new ModelAndView("products/list");
		modelAndView.addObject("products", products.list());
		return modelAndView;
	}

	@RequestMapping("/{id}")
	public ModelAndView edit(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView("products/edit");
		Product product = products.busca(id);
		modelAndView.addObject("produto", product);
		return modelAndView;
	}

}