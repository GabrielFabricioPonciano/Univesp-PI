import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ValidationErrors,
  AbstractControl,
  ReactiveFormsModule
} from '@angular/forms';
import { ProductService, Product } from '../productService';
import { MatDialogRef } from '@angular/material/dialog';
import { DecimalPipe, NgIf } from "@angular/common";

@Component({
  selector: 'app-product-form',
  standalone: true,
  templateUrl: './product-form.component.html',
  imports: [
    DecimalPipe,
    NgIf,
    ReactiveFormsModule
  ],
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {
  productForm!: FormGroup;
  unitPrice = 0.0;          // Nome corrigido
  finalPriceWithMargin = 0.0; // Nome corrigido
  unitPriceWithMargin = 0.0;  // Nome corrigido
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private dialogRef: MatDialogRef<ProductFormComponent>
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.productForm.valueChanges.subscribe(() => this.calculatePrices());
  }

  buildForm(): void {
    this.productForm = this.fb.group({
      productName: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      batchNumber: ['', Validators.required], // Nome corrigido
      productType: ['', Validators.required],
      expirationDate: ['', [Validators.required, this.futureDateValidator]], // Nome corrigido
      batchPrice: [0.0, [Validators.required, Validators.min(0)]], // Nome corrigido
      profitMargin: [0.0, [Validators.required, Validators.min(0)]], // Nome corrigido
      description: ['', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  calculatePrices(): void {
    const { quantity, batchPrice, profitMargin } = this.productForm.value;

    if (quantity > 0 && batchPrice >= 0 && profitMargin >= 0) {
      this.unitPrice = batchPrice / quantity;
      this.finalPriceWithMargin = batchPrice * (1 + profitMargin / 100);
      this.unitPriceWithMargin = this.finalPriceWithMargin / quantity;
    }
  }

  futureDateValidator(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const today = new Date();
    if (selectedDate <= today) {
      return { invalidDate: 'A data de validade deve ser uma data futura.' };
    }
    return null;
  }

  getErrorMessage(controlName: string): string | null {
    const control = this.productForm.get(controlName);
    if (control?.hasError('required')) return 'Este campo é obrigatório.';
    if (control?.hasError('min')) return 'O valor deve ser maior que 0.';
    if (control?.hasError('invalidDate')) return 'A data de validade deve ser uma data futura.';
    return null;
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const newProduct: Product = {
        ...this.productForm.value,
        status: 'ACTIVE'
      };

      this.productService.saveProduct(newProduct).subscribe({
        next: () => {
          this.productService.refreshProductList();
          this.dialogRef.close(true);
        },
        error: (error) => {
          console.error('Erro ao cadastrar produto!', error);
          this.errorMessage = 'Erro ao cadastrar produto. Verifique os dados e tente novamente.';
        }
      });
    } else {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios corretamente.';
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
export enum ProductStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  DISCONTINUED = 'DISCONTINUED'
}
