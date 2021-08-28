package org.epi.ws.model;

public class API_Model_MasterAsset {
	
	public String asset_id;
	public String asset_no;
	public String asset_name;
	public String nopol;
	public String nopol_before;
	public String nolam;
	public String asset_class;
	public String asset_type;
	public String nosin;
	public String norka;
	public String customer_id;
	public String remarks;
	public String asset_status;
	public String asset_file;
	public String manufacturing_year;
	public Integer project_id;
	public String po_id;
	
	public API_Model_MasterAsset(String asset_id,
			String asset_no,
			String asset_name,
			String nopol,
			String nopol_before,
			String nolam,
			String asset_class,
			String asset_type,
			String nosin,
			String norka,
			String customer_id,
			String remarks,
			String asset_status,
			String asset_file,
			String manufacturing_year,
			Integer project_id,
			String po_id) {
		
		
		this.asset_id = asset_id;
		this.asset_no = asset_no;
		this.asset_name = asset_name;
		this.nopol = nopol;
		this.nopol_before = nopol_before;
		this.nolam = nolam;
		this.asset_class = asset_class;
		this.asset_type = asset_type;
		this.nosin = nosin;
		this.norka = norka;
		this.customer_id = customer_id;
		this.remarks = remarks;
		this.asset_status = asset_status;
		this.asset_file = asset_file;
		this.manufacturing_year = manufacturing_year;
		this.project_id = project_id;
		this.po_id = po_id;
		
		
	}

}
