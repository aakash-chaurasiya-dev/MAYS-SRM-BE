$ErrorActionPreference = "Stop"
$baseDir = "d:\Mays\MAYS-SRM-BE\src\main\java\com\mays\srm"

# Define mappings from current locations to new domain locations
# Format: @( "SourceFileOrPattern", "DestinationDir" )
$mappings = @(
    # --- 1. TICKET ---
    # Controllers
    @("controller\TicketController.java", "ticket\controller"),
    @("controller\TicketLogsController.java", "ticket\controller"),
    @("controller\TicketTypeController.java", "ticket\controller"),
    # Entities
    @("entity\Ticket.java", "ticket\entities"),
    @("entity\TicketLogs.java", "ticket\entities"),
    @("entity\TicketType.java", "ticket\entities"),
    # Dao
    @("dao\TicketDao.java", "ticket\repository"),
    @("dao\TicketLogsDao.java", "ticket\repository"),
    @("dao\TicketTypeDao.java", "ticket\repository"),
    @("dao\custom\TicketDaoCustom.java", "ticket\repository\custom"),
    @("dao\custom\TicketLogsDaoCustom.java", "ticket\repository\custom"),
    @("dao\customImpl\TicketDaoCustomImpl.java", "ticket\repository\customImpl"),
    @("dao\customImpl\TicketLogsDaoCustomImpl.java", "ticket\repository\customImpl"),
    # Service
    @("service\TicketService.java", "ticket\service"),
    @("service\TicketTypeService.java", "ticket\service"),
    @("service\impl\TicketServiceImplementations\*", "ticket\service\impl"),
    @("service\impl\TicketTypeServiceImpl.java", "ticket\service\impl"),
    # DTOs
    @("dto\requestDTO\TicketDTO\*", "ticket\dto\request"),
    @("dto\requestDTO\TicketTypeRequestDTO.java", "ticket\dto\request"),
    @("dto\responseDTO\TicketDTO\*", "ticket\dto\resDTO"),
    @("dto\responseDTO\TicketLogsDTO\*", "ticket\dto\resDTO"),
    @("dto\responseDTO\TicketTypeResponseDTO.java", "ticket\dto\resDTO"),

    # --- 2. USER ---
    @("controller\UserMasterController.java", "user\controller"),
    @("controller\EmployeeController.java", "user\controller"),
    @("controller\EmployeeSpecController.java", "user\controller"),
    @("entity\UserMaster.java", "user\entities"),
    @("entity\Employee.java", "user\entities"),
    @("entity\EmployeeSpec.java", "user\entities"),
    @("dao\UserMasterDao.java", "user\repository"),
    @("dao\EmployeeDao.java", "user\repository"),
    @("dao\EmployeeSpecDao.java", "user\repository"),
    @("service\UserMasterService.java", "user\service"),
    @("service\EmployeeService.java", "user\service"),
    @("service\EmployeeSpecService.java", "user\service"),
    @("service\impl\UserMasterServiceImpl.java", "user\service\impl"),
    @("service\impl\EmployeeServiceImpl.java", "user\service\impl"),
    @("service\impl\EmployeeSpecServiceImpl.java", "user\service\impl"),
    @("dto\requestDTO\UserMasterRequestDTO.java", "user\dto\request"),
    @("dto\requestDTO\EmployeeRequestDTO.java", "user\dto\request"),
    @("dto\requestDTO\EmployeeSpecRequestDTO.java", "user\dto\request"),
    @("dto\responseDTO\UserMasterResponseDTO.java", "user\dto\resDTO"),
    @("dto\responseDTO\EmployeeResponseDTO.java", "user\dto\resDTO"),
    @("dto\responseDTO\EmployeeSpecResponseDTO.java", "user\dto\resDTO"),

    # --- 3. BILLING ---
    @("controller\BillingController.java", "billing\controller"),
    @("controller\PaymentModeDetailsController.java", "billing\controller"),
    @("controller\ServiceChargesController.java", "billing\controller"),
    @("controller\ChargeTypeController.java", "billing\controller"),
    @("entity\Billing.java", "billing\entities"),
    @("entity\PaymentModeDetails.java", "billing\entities"),
    @("entity\ServiceCharges.java", "billing\entities"),
    @("entity\ChargeType.java", "billing\entities"),
    @("dao\BillingDao.java", "billing\repository"),
    @("dao\PaymentModeDetailsDao.java", "billing\repository"),
    @("dao\ServiceChargesDao.java", "billing\repository"),
    @("dao\ChargeTypeDao.java", "billing\repository"),
    @("service\BillingService.java", "billing\service"),
    @("service\PaymentModeDetailsService.java", "billing\service"),
    @("service\ServiceChargesService.java", "billing\service"),
    @("service\ChargeTypeService.java", "billing\service"),
    @("service\impl\BillingServiceImpl.java", "billing\service\impl"),
    @("service\impl\PaymentModeDetailsServiceImpl.java", "billing\service\impl"),
    @("service\impl\ServiceChargesServiceImpl.java", "billing\service\impl"),
    @("service\impl\ChargeTypeServiceImpl.java", "billing\service\impl"),
    @("dto\requestDTO\BillingRequestDTO.java", "billing\dto\request"),
    @("dto\requestDTO\PaymentModeDetailsRequestDTO.java", "billing\dto\request"),
    @("dto\requestDTO\ServiceChargesRequestDTO.java", "billing\dto\request"),
    @("dto\requestDTO\ChargeTypeRequestDTO.java", "billing\dto\request"),
    @("dto\responseDTO\BillingResponseDTO.java", "billing\dto\resDTO"),
    @("dto\responseDTO\PaymentModeDetailsResponseDTO.java", "billing\dto\resDTO"),
    @("dto\responseDTO\ServiceChargesResponseDTO.java", "billing\dto\resDTO"),
    @("dto\responseDTO\ChargeTypeResponseDTO.java", "billing\dto\resDTO"),

    # --- 4. DEVICE ---
    @("controller\DeviceController.java", "device\controller"),
    @("controller\DeviceTypeController.java", "device\controller"),
    @("controller\DeviceModelController.java", "device\controller"),
    @("controller\BrandController.java", "device\controller"),
    @("entity\Device.java", "device\entities"),
    @("entity\DeviceType.java", "device\entities"),
    @("entity\DeviceModel.java", "device\entities"),
    @("entity\Brand.java", "device\entities"),
    @("dao\DeviceDao.java", "device\repository"),
    @("dao\DeviceTypeDao.java", "device\repository"),
    @("dao\DeviceModelDao.java", "device\repository"),
    @("dao\BrandDao.java", "device\repository"),
    @("service\DeviceService.java", "device\service"),
    @("service\DeviceTypeService.java", "device\service"),
    @("service\DeviceModelService.java", "device\service"),
    @("service\BrandService.java", "device\service"),
    @("service\impl\DeviceServiceImpl.java", "device\service\impl"),
    @("service\impl\DeviceTypeServiceImpl.java", "device\service\impl"),
    @("service\impl\DeviceModelServiceImpl.java", "device\service\impl"),
    @("service\impl\BrandServiceImpl.java", "device\service\impl"),
    @("dto\requestDTO\DeviceRequestDTO.java", "device\dto\request"),
    @("dto\requestDTO\DeviceTypeRequestDTO.java", "device\dto\request"),
    @("dto\requestDTO\DeviceModelRequestDTO.java", "device\dto\request"),
    @("dto\requestDTO\BrandRequestDTO.java", "device\dto\request"),
    @("dto\responseDTO\DeviceResponseDTO.java", "device\dto\resDTO"),
    @("dto\responseDTO\DeviceTypeResponseDTO.java", "device\dto\resDTO"),
    @("dto\responseDTO\DeviceModelResponseDTO.java", "device\dto\resDTO"),
    @("dto\responseDTO\BrandResponseDTO.java", "device\dto\resDTO"),

    # --- 5. INVENTORY ---
    @("controller\InventoryController.java", "inventory\controller"),
    @("controller\PartsController.java", "inventory\controller"),
    @("entity\Inventory.java", "inventory\entities"),
    @("entity\Parts.java", "inventory\entities"),
    @("dao\InventoryDao.java", "inventory\repository"),
    @("dao\PartsDao.java", "inventory\repository"),
    @("service\InventoryService.java", "inventory\service"),
    @("service\PartsService.java", "inventory\service"),
    @("service\impl\InventoryServiceImpl.java", "inventory\service\impl"),
    @("service\impl\PartsServiceImpl.java", "inventory\service\impl"),
    @("dto\requestDTO\InventoryRequestDTO.java", "inventory\dto\request"),
    @("dto\requestDTO\PartsRequestDTO.java", "inventory\dto\request"),
    @("dto\responseDTO\InventoryResponseDTO.java", "inventory\dto\resDTO"),
    @("dto\responseDTO\PartsResponseDTO.java", "inventory\dto\resDTO"),

    # --- 6. ORGANIZATION ---
    @("controller\BranchController.java", "organization\controller"),
    @("controller\DepartmentController.java", "organization\controller"),
    @("controller\StatusController.java", "organization\controller"),
    @("entity\Branch.java", "organization\entities"),
    @("entity\Department.java", "organization\entities"),
    @("entity\Status.java", "organization\entities"),
    @("dao\BranchDao.java", "organization\repository"),
    @("dao\DepartmentDao.java", "organization\repository"),
    @("dao\StatusDao.java", "organization\repository"),
    @("service\BranchService.java", "organization\service"),
    @("service\DepartmentService.java", "organization\service"),
    @("service\StatusService.java", "organization\service"),
    @("service\impl\BranchServiceImpl.java", "organization\service\impl"),
    @("service\impl\DepartmentServiceImpl.java", "organization\service\impl"),
    @("service\impl\StatusServiceImpl.java", "organization\service\impl"),
    @("dto\requestDTO\BranchRequestDTO.java", "organization\dto\request"),
    @("dto\requestDTO\DepartmentRequestDTO.java", "organization\dto\request"),
    @("dto\requestDTO\StatusRequestDTO.java", "organization\dto\request"),
    @("dto\responseDTO\BranchResponseDTO.java", "organization\dto\resDTO"),
    @("dto\responseDTO\DepartmentResponseDTO.java", "organization\dto\resDTO"),
    @("dto\responseDTO\StatusResponseDTO.java", "organization\dto\resDTO"),

    # --- 7. ENQUIRY ---
    @("controller\EnquiryController.java", "enquiry\controller"),
    @("entity\Enquiry.java", "enquiry\entities"),
    @("dao\EnquiryDao.java", "enquiry\repository"),
    @("service\EnquiryService.java", "enquiry\service"),
    @("service\impl\EnquiryServiceImpl.java", "enquiry\service\impl"),
    @("dto\requestDTO\EnquiryRequestDTO.java", "enquiry\dto\request"),
    @("dto\responseDTO\EnquiryResponseDTO.java", "enquiry\dto\resDTO"),

    # --- 8. CORE ---
    @("controller\AbstractController.java", "core\controller"),
    @("entity\AbstractEntity.java", "core\entities"),
    @("service\AbstractService.java", "core\service"),
    @("service\impl\AbstractServiceImpl.java", "core\service\impl")
)

# Step 1: Create directories and move files
foreach ($map in $mappings) {
    $srcPattern = Join-Path $baseDir $map[0]
    $destDir = Join-Path $baseDir $map[1]
    
    if (-not (Test-Path $destDir)) {
        New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    }

    # Use Resolve-Path to handle wildcards properly
    $srcFiles = @()
    if ($srcPattern -match '\*') {
        $parentDir = Split-Path $srcPattern
        $filter = Split-Path $srcPattern -Leaf
        if (Test-Path $parentDir) {
            $srcFiles = Get-ChildItem -Path $parentDir -Filter $filter
        }
    } else {
        if (Test-Path $srcPattern) {
            $srcFiles = @(Get-Item $srcPattern)
        }
    }

    foreach ($file in $srcFiles) {
        Move-Item -Path $file.FullName -Destination $destDir -Force
        Write-Host "Moved $($file.Name) to $($map[1])"
    }
}

Write-Host "File movement complete."
