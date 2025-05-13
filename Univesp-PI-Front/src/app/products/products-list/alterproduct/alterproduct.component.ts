import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {Product, ProductService, ProductUpdate} from "../../productService";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-alterproduct',
  standalone: true,
  imports: [
    ReactiveFormsModule,CommonModule
  ],
  templateUrl: './alterproduct.component.html',
  styleUrls: ['./alterproduct.component.scss']
})
export class AlterProductComponent implements OnInit {
  alterForm!: FormGroup;
  initialQuantity: number = 0;
  finalQuantity: number = 0;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AlterProductComponent>,
    private productService: ProductService,
    @Inject(MAT_DIALOG_DATA) public data: Product
  ) {
    this.initialQuantity = data.quantity;
    this.finalQuantity = this.initialQuantity;
  }

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.alterForm = this.fb.group({
      changeQuantity: [0, [Validators.required, Validators.min(-this.initialQuantity)]],
      finalQuantity: [{ value: this.initialQuantity, disabled: true }]
    });

    this.alterForm.get('changeQuantity')!.valueChanges.subscribe(change => {
      this.updateFinalQuantity(change);
    });
  }

  updateFinalQuantity(change: number): void {
    this.finalQuantity = this.initialQuantity + (change || 0);
  }

  adjustQuantity(amount: number): void {
    const currentChange = this.alterForm.get('changeQuantity')!.value || 0;
    this.alterForm.get('changeQuantity')!.setValue(currentChange + amount);
  }

  onSave(): void {
    if (this.alterForm.valid && this.finalQuantity > 0) {
      const {
        productId,
        unitPrice,
        finalPrice,
        createdAt,
        updatedAt,
        showActions,
        ...productData
      } = this.data;

      // Garante o tipo correto para expirationDate
      const expirationDate = productData.expirationDate
        ? new Date(productData.expirationDate).toISOString().split('T')[0]
        : undefined;

      const payload: ProductUpdate = {
        ...productData,
        quantity: this.finalQuantity,
        expirationDate, // Passa o valor formatado
        promotionId: productData.promotionId || null,
        status: productData.status as 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED' // Conversão de tipo
      };

      this.productService.updateProduct(payload, this.data.productId).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          this.errorMessage = `Erro ao atualizar: ${error.message}`;
          console.error('Erro:', error);
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
