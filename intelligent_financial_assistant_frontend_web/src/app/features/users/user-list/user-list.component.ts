import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDivider } from "@angular/material/divider";
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UserFormComponent } from '../user-form/user-form.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatMenuModule,
    MatTooltipModule,
    MatDivider,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSelectModule
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  displayedColumns: string[] = ['name', 'email', 'phone', 'cin', 'address', 'createdAt', 'status', 'actions'];
  dataSource!: MatTableDataSource<User>;
  loading = false;
  error: string | null = null;

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.dataSource.paginator = this.paginator;
  }

  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.dataSource.sort = this.sort;
  }

  paginator!: MatPaginator;
  sort!: MatSort;

  users: User[] = [];

  // Filters
  filterStatus: string = 'all';
  searchText: string = '';

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private router: Router
  ) {
    // Initialize dataSource here to ensure it exists for the setters
    this.dataSource = new MatTableDataSource<User>([]);
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = null;

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.dataSource.data = this.users;

        // Custom filter predicate
        this.dataSource.filterPredicate = (data: User, filter: string) => {
          const filterObj = JSON.parse(filter);
          const searchStr = filterObj.search.toLowerCase();
          const statusFilter = filterObj.status;

          const matchesSearch =
            data.firstName.toLowerCase().includes(searchStr) ||
            data.lastName.toLowerCase().includes(searchStr) ||
            data.email.toLowerCase().includes(searchStr) ||
            (!!data.phoneNumber && data.phoneNumber.includes(searchStr));

          const matchesStatus = statusFilter === 'all' ||
            (statusFilter === 'active' ? data.enabled : !data.enabled);

          return matchesSearch && matchesStatus;
        };

        if (this.paginator) {
          this.dataSource.paginator = this.paginator;
        }
        if (this.sort) {
          this.dataSource.sort = this.sort;
        }

        this.loading = false;
      },
      error: (error) => {
        this.error = error?.error?.message || 'Erreur lors du chargement des utilisateurs';
        this.loading = false;
        console.error('Erreur:', error);
      }
    });
  }

  ngAfterViewInit() {
    // Logic moved to setters
  }

  applyFilter(event?: Event) {
    if (event) {
      this.searchText = (event.target as HTMLInputElement).value;
    }

    const filterValue = JSON.stringify({
      search: this.searchText.trim(),
      status: this.filterStatus
    });

    this.dataSource.filter = filterValue;

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  onStatusFilterChange(value: string) {
    this.filterStatus = value;
    this.applyFilter();
  }

  viewUser(user: User): void {
    this.router.navigate(['/admin/users', user.id]);
  }

  editUser(user: User): void {
    const dialogRef = this.dialog.open(UserFormComponent, {
      width: '500px',
      data: { user: user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  deleteUser(user: User): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete User',
        message: `Are you sure you want to delete ${user.firstName} ${user.lastName}? This action cannot be undone.`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (err) => {
            console.error('Error deleting user', err);
            this.error = 'Failed to delete user';
          }
        });
      }
    });
  }

  addUser(): void {
    const dialogRef = this.dialog.open(UserFormComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }
}
